package br.com.tawandev.vendas.rest.controller;

import br.com.tawandev.vendas.domain.entities.Produto;
import br.com.tawandev.vendas.repositories.ProdutosRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@Api("Api Produtos")
public class ProdutoController {

    public static final String PRODUTO_NÃO_ENCONTRADO_PARA_AO_ID_INFORMADO = "Produto não encontrado par ao ID informado";
    public static final String ERRO_DE_VALIDAÇÃO = "Erro de validação";
    @Autowired
    private ProdutosRepository produtosRepository;

    @GetMapping("/{id}")
    @ApiOperation("Obtem detalhes de um produto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Produto encontrado com sucesso"),
            @ApiResponse(code = 404, message = PRODUTO_NÃO_ENCONTRADO_PARA_AO_ID_INFORMADO),
    })
    public Produto getById(@PathVariable @ApiParam("Id do produto") Integer id) {
        return produtosRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    @GetMapping
    public List<Produto> find(Produto filtro) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING);

        Example example = Example.of(filtro, matcher);
        return produtosRepository.findAll(example);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salva um produto")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Produto salvo com sucesso"),
            @ApiResponse(code = 400, message = ERRO_DE_VALIDAÇÃO)
    })
    public Produto save(@RequestBody @Valid Produto produto) {
        return produtosRepository.save(produto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Atualiza um produto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Produto atualizado com sucesso"),
            @ApiResponse(code = 400, message = ERRO_DE_VALIDAÇÃO),
            @ApiResponse(code = 404, message = PRODUTO_NÃO_ENCONTRADO_PARA_AO_ID_INFORMADO)
    })
    public void update(@PathVariable @ApiParam("Id do produto") Integer id, @RequestBody @Valid Produto produto) {
        produtosRepository.findById(id).map(p -> {
            produto.setId(p.getId());
            produtosRepository.save(produto);
            return produto;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Deleta um produto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Produto deletado com sucesso"),
            @ApiResponse(code = 404, message = PRODUTO_NÃO_ENCONTRADO_PARA_AO_ID_INFORMADO)
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @ApiParam("Id do produto") Integer id) {
        produtosRepository.findById(id).map(p -> {
            produtosRepository.save(p);
            return Void.TYPE;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }
}
