package fr.julien.charcuterieorders.service;

import fr.julien.charcuterieorders.model.OrderItem;
import fr.julien.charcuterieorders.model.Product;
import fr.julien.charcuterieorders.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final UserService userService;
    private final ProductService productService;
    private final OrderItemService orderItemService;

    public byte[] exportCommandes() throws IOException {
        List<User> clients = userService.getAllClients();
        List<Product> products = productService.getAllProducts();
        List<OrderItem> items = orderItemService.getAll();

        // Index quantités par userId → productId
        Map<Long, Map<Long, Integer>> quantities = new HashMap<>();
        for (OrderItem item : items) {
            quantities
                    .computeIfAbsent(item.getUser().getId(), k -> new HashMap<>())
                    .put(item.getProduct().getId(), item.getQuantity());
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Commandes");

            // Style en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Ligne d'en-tête : Produit | Client1 | Client2 | ...
            Row header = sheet.createRow(0);
            Cell firstCell = header.createCell(0);
            firstCell.setCellValue("Produit");
            firstCell.setCellStyle(headerStyle);

            for (int i = 0; i < clients.size(); i++) {
                Cell cell = header.createCell(i + 1);
                cell.setCellValue(clients.get(i).getName());
                cell.setCellStyle(headerStyle);
            }

            // Lignes produits
            for (int r = 0; r < products.size(); r++) {
                Product product = products.get(r);
                Row row = sheet.createRow(r + 1);

                row.createCell(0).setCellValue(product.getName());

                for (int c = 0; c < clients.size(); c++) {
                    Long userId = clients.get(c).getId();
                    Long productId = product.getId();

                    Integer qty = quantities
                            .getOrDefault(userId, new HashMap<>())
                            .get(productId);

                    if (qty != null) {
                        row.createCell(c + 1).setCellValue(qty);
                    }
                }
            }

            // Ajuste la largeur des colonnes
            for (int i = 0; i <= clients.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
}