package com.projetointegrador.petshop.application.venda;

import com.projetointegrador.petshop.domain.cliente.Cliente;
import com.projetointegrador.petshop.domain.cliente.ClienteRepository;
import com.projetointegrador.petshop.domain.exception.DomainException;
import com.projetointegrador.petshop.domain.produto.Produto;
import com.projetointegrador.petshop.domain.produto.ProdutoRepository;
import com.projetointegrador.petshop.domain.vendas.ItemVenda;
import com.projetointegrador.petshop.domain.vendas.Venda;
import com.projetointegrador.petshop.domain.vendas.VendaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VendaService {

    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository, ClienteRepository clienteRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    public Venda iniciarVenda(Long clienteId, Map<Long, Integer> produtosQuantidade) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new DomainException("Cliente não encontrado com ID: " + clienteId));

        List<ItemVenda> itens = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : produtosQuantidade.entrySet()) {
            Long produtoId = entry.getKey();
            Integer quantidade = entry.getValue();

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new DomainException("Produto não encontrado com ID: " + produtoId));
            
            // Verifica estoque antes de adicionar
            if (produto.getQuantidadeEstoque() < quantidade) {
                throw new DomainException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            itens.add(new ItemVenda(produto, quantidade));
        }

        Venda venda = new Venda(cliente, itens);
        return vendaRepository.save(venda);
    }

    public void concluirVenda(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new DomainException("Venda não encontrada com ID: " + vendaId));
        
        // Baixa no estoque
        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            // Recarrega produto para garantir consistência de estoque
            Produto produtoAtualizado = produtoRepository.findById(produto.getId())
                            .orElseThrow(() -> new DomainException("Produto não encontrado durante conclusão da venda."));
            
            produtoAtualizado.decrementarEstoque(item.getQuantidade());
            produtoRepository.save(produtoAtualizado);
        }

        venda.concluirVenda();
        vendaRepository.save(venda);
    }

    public void cancelarVenda(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new DomainException("Venda não encontrada com ID: " + vendaId));
        
        venda.cancelarVenda();
        vendaRepository.save(venda);
    }

    public List<Venda> listarTodas() {
        return vendaRepository.findAll();
    }

    public Optional<Venda> buscarPorId(Long id) {
        return vendaRepository.findById(id);
    }
}
