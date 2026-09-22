package Barbearia.agendamento.informacoes;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "agendamentos")
public class agendamento {

    @Id
    private String id;

    private String nome;

    private LocalDateTime horario;

    private String telefone;

    private List<String> servicos;
}
