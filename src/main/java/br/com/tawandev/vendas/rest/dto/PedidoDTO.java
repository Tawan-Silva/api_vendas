package br.com.tawandev.vendas.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDTO {

    @NotNull(message = "Informe o código do cliente.")
    private Integer Cliente;
    @NotNull(message = "Campo Total do pedido é obrigatório.")
    private BigDecimal total;
    private List<ItemPedidoDTO> itens;
}
