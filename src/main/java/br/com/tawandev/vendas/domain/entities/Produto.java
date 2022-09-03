package br.com.tawandev.vendas.domain.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "descricao")
    @NotEmpty(message = "Campo Descrição é obrigatório.")
    private String descricao;

    @Column(name = "proco_unitario")
    @NotNull(message = "Campo Preço é obrigatório.")
    private BigDecimal preco;

}
