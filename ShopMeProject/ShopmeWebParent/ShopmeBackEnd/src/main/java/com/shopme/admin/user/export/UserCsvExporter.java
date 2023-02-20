package com.shopme.admin.user.export;

import com.shopme.admin.user.common.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

@Slf4j
public class UserCsvExporter extends AbstractExporter {

    public void export(List<User> userList, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response, "text/csv", ".csv", "users_");

        Writer writer = new OutputStreamWriter(response.getOutputStream(), "utf-8");
        writer.write('\uFEFF');

        ICsvBeanWriter csvWriter = new CsvBeanWriter(writer,
                CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"User ID", "Email", "First Name", "Last Name", "Roles", "Enabled"};
        String[] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};

        csvWriter.writeHeader(csvHeader);

        userList.forEach(i -> {
            try {
                csvWriter.write(i, fieldMapping);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });

        csvWriter.close();

    }
}
