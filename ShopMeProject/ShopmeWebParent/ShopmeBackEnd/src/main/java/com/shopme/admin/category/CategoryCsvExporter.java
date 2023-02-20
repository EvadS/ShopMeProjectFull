package com.shopme.admin.category;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.shopme.admin.user.common.entity.Category;
import com.shopme.admin.user.export.AbstractExporter;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;


public class CategoryCsvExporter extends AbstractExporter {
    public void export(List<Category> listCategories, HttpServletResponse response)
            throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "categories_");

        Writer writer = new OutputStreamWriter(response.getOutputStream(), "utf-8");
        writer.write('\uFEFF');

        ICsvBeanWriter csvWriter = new CsvBeanWriter(writer,
                CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Category ID", "Category Name"};
        String[] fieldMapping = {"id", "name"};

        csvWriter.writeHeader(csvHeader);

        for (Category category : listCategories) {
            category.setName(category.getName().replace("--", "  "));
            csvWriter.write(category, fieldMapping);
        }

        csvWriter.close();
    }
}
