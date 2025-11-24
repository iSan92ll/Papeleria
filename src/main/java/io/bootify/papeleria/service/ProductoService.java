package io.bootify.papeleria.service;

import io.bootify.papeleria.domain.Producto;
import io.bootify.papeleria.events.BeforeDeleteProducto;
import io.bootify.papeleria.model.ProductoDTO;
import io.bootify.papeleria.repos.ProductoRepository;
import io.bootify.papeleria.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ApplicationEventPublisher publisher;

    public ProductoService(final ProductoRepository productoRepository,
            final ApplicationEventPublisher publisher) {
        this.productoRepository = productoRepository;
        this.publisher = publisher;
    }

    public List<ProductoDTO> findAll() {
        final List<Producto> productoes = productoRepository.findAll(Sort.by("idProducto"));
        return productoes.stream()
                .map(producto -> mapToDTO(producto, new ProductoDTO()))
                .toList();
    }

    public ProductoDTO get(final Long idProducto) {
        return productoRepository.findById(idProducto)
                .map(producto -> mapToDTO(producto, new ProductoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ProductoDTO productoDTO) {
        final Producto producto = new Producto();
        mapToEntity(productoDTO, producto);
        return productoRepository.save(producto).getIdProducto();
    }

    public void update(final Long idProducto, final ProductoDTO productoDTO) {
        final Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(NotFoundException::new);
        mapToEntity(productoDTO, producto);
        productoRepository.save(producto);
    }

    public void delete(final Long idProducto) {
        final Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteProducto(idProducto));
        productoRepository.delete(producto);
    }

    private ProductoDTO mapToDTO(final Producto producto, final ProductoDTO productoDTO) {
        productoDTO.setIdProducto(producto.getIdProducto());
        productoDTO.setNombre(producto.getNombre());
        productoDTO.setDescripcion(producto.getDescripcion());
        productoDTO.setPrecio(producto.getPrecio());
        productoDTO.setStock(producto.getStock());
        productoDTO.setActivo(producto.getActivo());
        productoDTO.setImagenUrl(producto.getImagenUrl());
        return productoDTO;
    }

    private Producto mapToEntity(final ProductoDTO productoDTO, final Producto producto) {
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStock(productoDTO.getStock());
        producto.setActivo(productoDTO.getActivo());
        producto.setImagenUrl(productoDTO.getImagenUrl());
        return producto;
    }

}
