package br.com.tawandev.vendas.repositories;

import br.com.tawandev.vendas.domain.entities.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPedidosRepository extends JpaRepository<ItemPedido, Integer> {
}
