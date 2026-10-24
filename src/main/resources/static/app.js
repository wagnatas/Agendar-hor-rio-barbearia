'use strict';
const $ = (id) => document.getElementById(id);
const state = {page: 0, keyword: '', items: [], totalPages: 0, loading: false, editing: null, deleting: null, request: 0};
const form = $('appointment-form');
const dateFormat = new Intl.DateTimeFormat('pt-BR', {day: '2-digit', month: 'short', year: 'numeric'});
const timeFormat = new Intl.DateTimeFormat('pt-BR', {hour: '2-digit', minute: '2-digit'});

function notice(message, error = false) {
  $('notice').textContent = message;
  $('notice').classList.toggle('error', error);
  $('notice').hidden = !message;
}

async function api(path, options = {}) {
  let response;
  try { response = await fetch('/agenda' + path, options); }
  catch { throw new Error('Não foi possível conectar. Verifique se o servidor está em execução.'); }
  const text = await response.text();
  let data;
  try { data = text ? JSON.parse(text) : null; } catch { data = null; }
  if (!response.ok) {
    const fallback = {400: 'Confira os campos e tente novamente.', 404: 'Agendamento não encontrado. Atualize a lista.', 409: 'Este horário já está preenchido. Escolha outro horário.'};
    throw new Error(data?.detail || data?.message || fallback[response.status] || 'Não foi possível concluir a operação. Tente novamente.');
  }
  return data;
}

function cell(text, tag = 'td') {
  const element = document.createElement(tag);
  element.textContent = text;
  return element;
}

function render() {
  $('rows').replaceChildren();
  for (const item of state.items) {
    const row = document.createElement('tr');
    const client = cell('');
    client.append(cell(item.nome, 'strong'), cell(item.telefone, 'small'));
    const date = new Date(item.dataHora);
    const when = cell('');
    if (Number.isNaN(date.getTime())) when.textContent = 'Data indisponível';
    else when.append(cell(dateFormat.format(date), 'strong'), cell(timeFormat.format(date), 'small'));
    const services = cell('');
    for (const service of item.servicos || []) {
      const badge = cell(service, 'span');
      badge.className = 'tag';
      services.append(badge);
    }
    const actions = cell('');
    const buttons = document.createElement('div');
    buttons.className = 'row-actions';
    const edit = cell('Editar', 'button');
    edit.type = 'button';
    edit.setAttribute('aria-label', `Editar agendamento de ${item.nome}`);
    edit.onclick = () => openEditor(item);
    const remove = cell('Excluir', 'button');
    remove.type = 'button';
    remove.className = 'remove';
    remove.setAttribute('aria-label', `Excluir agendamento de ${item.nome}`);
    remove.onclick = () => {
      state.deleting = item;
      $('delete-description').textContent = `O agendamento de ${item.nome} será excluído.`;
      $('delete-error').hidden = true;
      $('confirmation').showModal();
      $('keep').focus();
    };
    buttons.append(edit, remove);
    actions.append(buttons);
    row.append(client, when, services, actions);
    $('rows').append(row);
  }
  $('empty').hidden = state.items.length > 0 || state.loading;
}

async function load() {
  const request = ++state.request;
  state.loading = true;
  $('rows').setAttribute('aria-busy', 'true');
  $('previous').disabled = $('next').disabled = true;
  $('page-info').textContent = 'Carregando agendamentos…';
  const params = new URLSearchParams();
  if (state.keyword) params.set('keyword', state.keyword);
  if ($('size').value !== 'all') {
    params.set('page', state.page);
    params.set('size', $('size').value);
  }
  try {
    const data = await api('?' + params);
    if (request !== state.request) return;
    if (!data || !Array.isArray(data.content)) throw new Error('O servidor retornou uma lista inválida.');
    state.totalPages = data.totalPages;
    if (state.page > 0 && state.page >= data.totalPages) {
      state.page = Math.max(0, data.totalPages - 1);
      return load();
    }
    state.items = data.content;
    state.loading = false;
    render();
    $('total').textContent = `${data.totalElements} no total`;
    $('page-info').textContent = $('size').value === 'all' ? `${data.totalElements} agendamento(s)` : `Página ${data.totalPages ? state.page + 1 : 0} de ${data.totalPages}`;
    $('previous').disabled = state.page === 0 || $('size').value === 'all';
    $('next').disabled = state.page + 1 >= data.totalPages || $('size').value === 'all';
  } catch (error) {
    if (request !== state.request) return;
    state.items = [];
    state.loading = false;
    render();
    $('empty').hidden = true;
    $('total').textContent = '—';
    $('page-info').textContent = 'Falha ao carregar. Use Buscar para tentar novamente.';
    notice(error.message, true);
  } finally {
    if (request === state.request) {
      state.loading = false;
      $('rows').setAttribute('aria-busy', 'false');
    }
  }
}

function openEditor(item = null) {
  state.editing = item;
  form.reset();
  $('form-error').hidden = true;
  $('editor-title').textContent = item ? 'Editar agendamento' : 'Novo agendamento';
  if (item) {
    form.elements.nome.value = item.nome;
    form.elements.telefone.value = item.telefone;
    form.elements.dataHora.value = item.dataHora.slice(0, 16);
    form.elements.servicos.value = (item.servicos || []).join(', ');
  }
  $('editor').showModal();
}

$('new').onclick = () => openEditor();
$('close').onclick = $('cancel').onclick = () => $('editor').close();
$('keep').onclick = () => $('confirmation').close();
$('search').onsubmit = (event) => {
  event.preventDefault();
  notice('');
  state.keyword = $('keyword').value.trim();
  state.page = 0;
  load();
};
$('size').onchange = () => { state.page = 0; notice(''); load(); };
$('previous').onclick = () => { if (state.page > 0) { state.page--; load(); } };
$('next').onclick = () => { if (state.page + 1 < state.totalPages) { state.page++; load(); } };

form.onsubmit = async (event) => {
  event.preventDefault();
  const payload = {
    nome: form.elements.nome.value.trim(),
    telefone: form.elements.telefone.value.trim(),
    dataHora: form.elements.dataHora.value,
    servicos: form.elements.servicos.value.split(',').map(value => value.trim()).filter(Boolean)
  };
  if (!payload.nome || !payload.telefone || !payload.servicos.length) {
    $('form-error').textContent = 'Informe nome, telefone e pelo menos um serviço.';
    $('form-error').hidden = false;
    return;
  }
  const editing = state.editing;
  $('save').disabled = $('close').disabled = $('cancel').disabled = true;
  $('save').textContent = 'Salvando…';
  $('editor').oncancel = (event) => event.preventDefault();
  try {
    await api(editing ? '/atualizar/' + encodeURIComponent(editing.id) : '', {
      method: editing ? 'PATCH' : 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(payload)
    });
    $('editor').close();
    notice(editing ? 'Agendamento atualizado.' : 'Agendamento criado com sucesso.');
    await load();
  } catch (error) {
    $('form-error').textContent = error.message;
    $('form-error').hidden = false;
  } finally {
    $('save').disabled = $('close').disabled = $('cancel').disabled = false;
    $('save').textContent = 'Salvar agendamento';
    $('editor').oncancel = null;
  }
};

$('delete-form').onsubmit = async (event) => {
  event.preventDefault();
  $('delete').disabled = $('keep').disabled = true;
  $('delete').textContent = 'Excluindo…';
  $('confirmation').oncancel = (event) => event.preventDefault();
  try {
    await api('/excluir/' + encodeURIComponent(state.deleting.id), {method: 'DELETE'});
    $('confirmation').close();
    notice('Agendamento excluído.');
    await load();
  } catch (error) {
    $('delete-error').textContent = error.message;
    $('delete-error').hidden = false;
  } finally {
    $('delete').disabled = $('keep').disabled = false;
    $('delete').textContent = 'Excluir';
    $('confirmation').oncancel = null;
  }
};

load();
