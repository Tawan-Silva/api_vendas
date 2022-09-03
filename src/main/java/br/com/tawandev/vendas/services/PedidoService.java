package br.com.tawandev.vendas.services;

import br.com.tawandev.vendas.domain.entities.Pedido;
import br.com.tawandev.vendas.domain.enums.StatusPedido;
import br.com.tawandev.vendas.rest.dto.PedidoDTO;

import java.util.Optional;

public interface PedidoService {
    Pedido salvar(PedidoDTO dto);
    Optional<Pedido> obterPedidoCompleto(Integer id);
    void atualizaPedido(Integer id, StatusPedido statusPedido);
}
