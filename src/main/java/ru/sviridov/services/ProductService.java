package ru.sviridov.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sviridov.entities.Product;
import ru.sviridov.fabric.RepositoryFabric;
import ru.sviridov.mappers.JdbcMapper;
import ru.sviridov.repositories.ProductRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService implements EntityService<Product> {

    private final ProductRepository productRepository;
    private final JdbcMapper mapper;


    public ProductService() {
        mapper = new JdbcMapper();
        this.productRepository = RepositoryFabric.createProductRepository();
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
    public boolean insert(HttpServletRequest req) throws IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        Product product = objectMapper.readValue(body, Product.class);
        return productRepository.insert(product);
    }

    @Override
    public long update(long id, HttpServletRequest req) throws IOException {
        Product product = mapper.mapJsonToProduct(req);
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
}
