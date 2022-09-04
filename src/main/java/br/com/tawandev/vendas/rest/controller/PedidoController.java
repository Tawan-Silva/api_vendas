package br.com.tawandev.vendas.rest.controller;

import br.com.tawandev.vendas.domain.entities.ItemPedido;
import br.com.tawandev.vendas.domain.entities.Pedido;
import br.com.tawandev.vendas.domain.enums.StatusPedido;
import br.com.tawandev.vendas.rest.dto.AtualizacaoStatusPedidoDTO;
import br.com.tawandev.vendas.rest.dto.InformacaoItemPedidoDTO;
import br.com.tawandev.vendas.rest.dto.InformacoesPedidoDTO;
import br.com.tawandev.vendas.rest.dto.PedidoDTO;
import br.com.tawandev.vendas.services.PedidoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/pedidos")
@Api("Api pedidos")
public class PedidoController {

    public static final String PEDIDO_NÃO_ENCONTRADO_PARA_O_ID_INFORMADO = "Pedido não encontrado para o ID informado";
    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salva um novo Pedido")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Pedido salvo com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação"),
    })
    public Integer save(@RequestBody @Valid PedidoDTO dto) {
        Pedido pedido = pedidoService.salvar(dto);
        return pedido.getId();
    }

    @GetMapping("/{id}")
    @ApiOperation("Obter detalhes de um Pedido")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Pedido encontrado"),
            @ApiResponse(code = 404, message = PEDIDO_NÃO_ENCONTRADO_PARA_O_ID_INFORMADO),
    })
    public InformacoesPedidoDTO getById(@PathVariable @ApiParam("Id do pedido") Integer id ) {
        return pedidoService
                .obterPedidoCompleto(id)
                .map(p -> converter(p))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Atualiza status de um Pedido")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Atatus do Pedido atualizado com sucesso"),
            @ApiResponse(code = 404, message = PEDIDO_NÃO_ENCONTRADO_PARA_O_ID_INFORMADO),
    })
    public void updateStatus(@PathVariable @ApiParam("Id do pedido") Integer id,
                             @RequestBody AtualizacaoStatusPedidoDTO dto) {
        String novoStatus = dto.getNovoStatus();
        pedidoService.atualizaPedido(id, StatusPedido.valueOf(novoStatus));
    }

    private InformacoesPedidoDTO converter(Pedido pedido) {
        return InformacoesPedidoDTO
                .builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .total(pedido.getTotal())
                .status(pedido.getStatus().name())
                .itens(converter(pedido.getItens()))
                .build();
    }

    private List<InformacaoItemPedidoDTO> converter(List<ItemPedido> itens) {
        if(CollectionUtils.isEmpty(itens)) {
            return Collections.emptyList();
        }

        return itens.stream().map(
                item -> InformacaoItemPedidoDTO
                        .builder().decricaoProduto(item.getProduto().getDescricao())
                        .precoUnitario(item.getProduto().getPreco())
                        .quantidade(item.getQuantidade())
                        .build()
        ).collect(Collectors.toList());
    }
}

