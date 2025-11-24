package io.bootify.papeleria.service;

import io.bootify.papeleria.domain.Producto;
import io.bootify.papeleria.domain.Venta;
import io.bootify.papeleria.domain.VentaItem;
import io.bootify.papeleria.events.BeforeDeleteProducto;
import io.bootify.papeleria.events.BeforeDeleteVenta;
import io.bootify.papeleria.model.VentaItemDTO;
import io.bootify.papeleria.repos.ProductoRepository;
import io.bootify.papeleria.repos.VentaItemRepository;
import io.bootify.papeleria.repos.VentaRepository;
import io.bootify.papeleria.util.NotFoundException;
import io.bootify.papeleria.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class VentaItemService {

    private final VentaItemRepository ventaItemRepository;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaItemService(final VentaItemRepository ventaItemRepository,
            final VentaRepository ventaRepository, final ProductoRepository productoRepository) {
        this.ventaItemRepository = ventaItemRepository;
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    public List<VentaItemDTO> findAll() {
        final List<VentaItem> ventaItems = ventaItemRepository.findAll(Sort.by("idVentaItem"));
        return ventaItems.stream()
                .map(ventaItem -> mapToDTO(ventaItem, new VentaItemDTO()))
                .toList();
    }

    public VentaItemDTO get(final Long idVentaItem) {
        return ventaItemRepository.findById(idVentaItem)
                .map(ventaItem -> mapToDTO(ventaItem, new VentaItemDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final VentaItemDTO ventaItemDTO) {
        final VentaItem ventaItem = new VentaItem();
        mapToEntity(ventaItemDTO, ventaItem);
        return ventaItemRepository.save(ventaItem).getIdVentaItem();
    }

    public void update(final Long idVentaItem, final VentaItemDTO ventaItemDTO) {
        final VentaItem ventaItem = ventaItemRepository.findById(idVentaItem)
                .orElseThrow(NotFoundException::new);
        mapToEntity(ventaItemDTO, ventaItem);
        ventaItemRepository.save(ventaItem);
    }

    public void delete(final Long idVentaItem) {
        final VentaItem ventaItem = ventaItemRepository.findById(idVentaItem)
                .orElseThrow(NotFoundException::new);
        ventaItemRepository.delete(ventaItem);
    }

    private VentaItemDTO mapToDTO(final VentaItem ventaItem, final VentaItemDTO ventaItemDTO) {
        ventaItemDTO.setIdVentaItem(ventaItem.getIdVentaItem());
        ventaItemDTO.setNombreProducto(ventaItem.getNombreProducto());
        ventaItemDTO.setCantidad(ventaItem.getCantidad());
        ventaItemDTO.setPrecioUnitario(ventaItem.getPrecioUnitario());
        ventaItemDTO.setVenta(ventaItem.getVenta() == null ? null : ventaItem.getVenta().getIdVenta());
        ventaItemDTO.setProducto(ventaItem.getProducto() == null ? null : ventaItem.getProducto().getIdProducto());
        return ventaItemDTO;
    }

    private VentaItem mapToEntity(final VentaItemDTO ventaItemDTO, final VentaItem ventaItem) {
        ventaItem.setNombreProducto(ventaItemDTO.getNombreProducto());
        ventaItem.setCantidad(ventaItemDTO.getCantidad());
        ventaItem.setPrecioUnitario(ventaItemDTO.getPrecioUnitario());
        final Venta venta = ventaItemDTO.getVenta() == null ? null : ventaRepository.findById(ventaItemDTO.getVenta())
                .orElseThrow(() -> new NotFoundException("venta not found"));
        ventaItem.setVenta(venta);
        final Producto producto = ventaItemDTO.getProducto() == null ? null : productoRepository.findById(ventaItemDTO.getProducto())
                .orElseThrow(() -> new NotFoundException("producto not found"));
        ventaItem.setProducto(producto);
        return ventaItem;
    }

    @EventListener(BeforeDeleteVenta.class)
    public void on(final BeforeDeleteVenta event) {
        final ReferencedException referencedException = new ReferencedException();
        final VentaItem ventaVentaItem = ventaItemRepository.findFirstByVentaIdVenta(event.getIdVenta());
        if (ventaVentaItem != null) {
            referencedException.setKey("venta.ventaItem.venta.referenced");
            referencedException.addParam(ventaVentaItem.getIdVentaItem());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteProducto.class)
    public void on(final BeforeDeleteProducto event) {
        final ReferencedException referencedException = new ReferencedException();
        final VentaItem productoVentaItem = ventaItemRepository.findFirstByProductoIdProducto(event.getIdProducto());
        if (productoVentaItem != null) {
            referencedException.setKey("producto.ventaItem.producto.referenced");
            referencedException.addParam(productoVentaItem.getIdVentaItem());
            throw referencedException;
        }
    }

}
