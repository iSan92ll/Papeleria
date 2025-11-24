package io.bootify.papeleria.service;

import io.bootify.papeleria.domain.Carrito;
import io.bootify.papeleria.domain.CarritoItem;
import io.bootify.papeleria.domain.Producto;
import io.bootify.papeleria.events.BeforeDeleteCarrito;
import io.bootify.papeleria.events.BeforeDeleteProducto;
import io.bootify.papeleria.model.CarritoItemDTO;
import io.bootify.papeleria.repos.CarritoItemRepository;
import io.bootify.papeleria.repos.CarritoRepository;
import io.bootify.papeleria.repos.ProductoRepository;
import io.bootify.papeleria.util.NotFoundException;
import io.bootify.papeleria.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CarritoItemService {

    private final CarritoItemRepository carritoItemRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    public CarritoItemService(final CarritoItemRepository carritoItemRepository,
            final CarritoRepository carritoRepository,
            final ProductoRepository productoRepository) {
        this.carritoItemRepository = carritoItemRepository;
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }

    public List<CarritoItemDTO> findAll() {
        final List<CarritoItem> carritoItems = carritoItemRepository.findAll(Sort.by("idCarritoItem"));
        return carritoItems.stream()
                .map(carritoItem -> mapToDTO(carritoItem, new CarritoItemDTO()))
                .toList();
    }

    public CarritoItemDTO get(final Long idCarritoItem) {
        return carritoItemRepository.findById(idCarritoItem)
                .map(carritoItem -> mapToDTO(carritoItem, new CarritoItemDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CarritoItemDTO carritoItemDTO) {
        final CarritoItem carritoItem = new CarritoItem();
        mapToEntity(carritoItemDTO, carritoItem);
        return carritoItemRepository.save(carritoItem).getIdCarritoItem();
    }

    public void update(final Long idCarritoItem, final CarritoItemDTO carritoItemDTO) {
        final CarritoItem carritoItem = carritoItemRepository.findById(idCarritoItem)
                .orElseThrow(NotFoundException::new);
        mapToEntity(carritoItemDTO, carritoItem);
        carritoItemRepository.save(carritoItem);
    }

    public void delete(final Long idCarritoItem) {
        final CarritoItem carritoItem = carritoItemRepository.findById(idCarritoItem)
                .orElseThrow(NotFoundException::new);
        carritoItemRepository.delete(carritoItem);
    }

    private CarritoItemDTO mapToDTO(final CarritoItem carritoItem,
            final CarritoItemDTO carritoItemDTO) {
        carritoItemDTO.setIdCarritoItem(carritoItem.getIdCarritoItem());
        carritoItemDTO.setCantidad(carritoItem.getCantidad());
        carritoItemDTO.setCarrito(carritoItem.getCarrito() == null ? null : carritoItem.getCarrito().getIdCarrito());
        carritoItemDTO.setProducto(carritoItem.getProducto() == null ? null : carritoItem.getProducto().getIdProducto());
        return carritoItemDTO;
    }

    private CarritoItem mapToEntity(final CarritoItemDTO carritoItemDTO,
            final CarritoItem carritoItem) {
        carritoItem.setCantidad(carritoItemDTO.getCantidad());
        final Carrito carrito = carritoItemDTO.getCarrito() == null ? null : carritoRepository.findById(carritoItemDTO.getCarrito())
                .orElseThrow(() -> new NotFoundException("carrito not found"));
        carritoItem.setCarrito(carrito);
        final Producto producto = carritoItemDTO.getProducto() == null ? null : productoRepository.findById(carritoItemDTO.getProducto())
                .orElseThrow(() -> new NotFoundException("producto not found"));
        carritoItem.setProducto(producto);
        return carritoItem;
    }

    @EventListener(BeforeDeleteCarrito.class)
    public void on(final BeforeDeleteCarrito event) {
        final ReferencedException referencedException = new ReferencedException();
        final CarritoItem carritoCarritoItem = carritoItemRepository.findFirstByCarritoIdCarrito(event.getIdCarrito());
        if (carritoCarritoItem != null) {
            referencedException.setKey("carrito.carritoItem.carrito.referenced");
            referencedException.addParam(carritoCarritoItem.getIdCarritoItem());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteProducto.class)
    public void on(final BeforeDeleteProducto event) {
        final ReferencedException referencedException = new ReferencedException();
        final CarritoItem productoCarritoItem = carritoItemRepository.findFirstByProductoIdProducto(event.getIdProducto());
        if (productoCarritoItem != null) {
            referencedException.setKey("producto.carritoItem.producto.referenced");
            referencedException.addParam(productoCarritoItem.getIdCarritoItem());
            throw referencedException;
        }
    }

}
