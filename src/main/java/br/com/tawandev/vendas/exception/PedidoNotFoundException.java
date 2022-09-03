package br.com.tawandev.vendas.exception;

public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(String msg) {
        super(msg);
    }
}
