package ru.velkomfood.mm.mrp.uploader.output;

import ru.velkomfood.mm.mrp.uploader.MrpUploaderComponent;

import java.sql.SQLException;

public interface DataWriter extends MrpUploaderComponent {
    void save(String dataAddress) throws SQLException;
}
