package Barbearia.agendamento.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;


import java.util.List;

@Getter
@Setter
public class criarAgendaDTO {

    @NotBlank
    private String nome;

    @NotBlank
    private String horario;

    @NotBlank
    private String telefone;

    @NotNull
    private List<String> servicos;
}
