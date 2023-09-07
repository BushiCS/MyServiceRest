package ru.sviridov.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Product;
import ru.sviridov.fabric.RepositoryFabric;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.repositories.ProductRepository;
import ru.sviridov.repositories.UserRepository;
import ru.sviridov.sessions.SessionManager;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService implements EntityService<Product> {

    private final ProductRepository productRepository;


    public ProductService() {
        this.productRepository = RepositoryFabric.createProductRepository();
    }

    public ProductService(SessionManager manager) {
        productRepository = new ProductRepository(manager);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Product> getAll() {
        return productRepository.getAll();
    }

    @Override
    public Product getById(long id) {
        return productRepository.getById(id);
    }

    @Override
    public boolean insert(Product product) throws IOException {
        return productRepository.insert(product);
    }

    @Override
    public long update(long id, Product product) throws IOException {
        return productRepository.update(id, product);
    }

    @Override
    public long deleteById(long id) {
        return productRepository.delete(id);
    }

    public Product getUserProductByProductId(long productId, long userId) {
        return productRepository.getUserProductByProductId(productId, userId);
    }

    public List<Product> getUserProducts(long userId) {
        return productRepository.getUserProducts(userId);
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }
}


