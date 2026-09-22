package Barbearia.agendamento.informacoes;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "agendamentos")
public class agendamento {

    @Id
    private String id;

    private String nome;

    private LocalDate data;

    private LocalTime hora;

    private String telefone;

    private List<String> servicos;
}
