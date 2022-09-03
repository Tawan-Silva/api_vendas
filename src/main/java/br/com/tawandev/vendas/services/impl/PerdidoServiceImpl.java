package br.com.tawandev.vendas.services.impl;

import br.com.tawandev.vendas.domain.entities.Cliente;
import br.com.tawandev.vendas.domain.entities.ItemPedido;
import br.com.tawandev.vendas.domain.entities.Pedido;
import br.com.tawandev.vendas.domain.entities.Produto;
import br.com.tawandev.vendas.domain.enums.StatusPedido;
import br.com.tawandev.vendas.exception.PedidoNotFoundException;
import br.com.tawandev.vendas.exception.RegraNegocioException;
import br.com.tawandev.vendas.repositories.ClientesRepository;
import br.com.tawandev.vendas.repositories.ItemPedidosRepository;
import br.com.tawandev.vendas.repositories.PedidosRepository;
import br.com.tawandev.vendas.repositories.ProdutosRepository;
import br.com.tawandev.vendas.rest.dto.ItemPedidoDTO;
import br.com.tawandev.vendas.rest.dto.PedidoDTO;
import br.com.tawandev.vendas.services.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerdidoServiceImpl implements PedidoService{

    private final PedidosRepository pedidosRepository;
    private final ClientesRepository clientesRepository;
    private final ProdutosRepository produtosRepository;
    private  final ItemPedidosRepository itemPedidosRepository;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();

        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));

        Pedido pedido = new Pedido();

        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itemPedidos = converterItens(pedido, dto.getItens());
        pedidosRepository.save(pedido);
        itemPedidosRepository.saveAll(itemPedidos);
        pedido.setItens(itemPedidos);
        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return pedidosRepository.findByIdFechItens(id);
    }

    @Override
    @Transactional
    public void atualizaPedido(Integer id, StatusPedido statusPedido) {
        pedidosRepository
                .findById(id)
                .map(pedido -> {
                    pedido.setStatus(statusPedido);
                    return pedidosRepository.save(pedido);
                }).orElseThrow(() -> new PedidoNotFoundException("Pedido não encontrado: " + id));
    }

    private List<ItemPedido> converterItens(Pedido pedido, List<ItemPedidoDTO> itens) {
        if (itens.isEmpty()) {
            throw new RegraNegocioException(("Não é possível realizar um pedido sem itens."));
        }
        return itens
                .stream()
                .map(dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository
                            .findById(idProduto)
                            .orElseThrow(() -> new RegraNegocioException("Código de produto inválido: " + idProduto));

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    itemPedido.setQuantidade(dto.getQuantidade());
                    return itemPedido;
                }).collect(Collectors.toList());
    }
}
