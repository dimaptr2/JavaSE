package ru.velkomfood.mrp.client.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dpetrov on 16.08.2016.
 */
public class MaterialCache {

    private static MaterialCache instance;
    private List<Material> materialList;
    private int indexOfMaterial;

    private MaterialCache() {
        materialList = new ArrayList<>();
    }

    public static MaterialCache getInstance() {
        if (instance == null) instance = new MaterialCache();
        return instance;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }

    public void setIndexOfMaterial(int indexOfMaterial) {
        this.indexOfMaterial = indexOfMaterial;
    }

    public int getIndexOfMaterial() {
        return indexOfMaterial;
    }

    public void fillMaterialsList(Statement stmt, String mask) throws SQLException {

        final String SQL = "SELECT matnr, maktx FROM mara" +
                " WHERE matnr LIKE " + "\'" + mask + "%\' ORDER BY matnr";

        if (!materialList.isEmpty()) materialList.clear();

        ResultSet rs = stmt.executeQuery(SQL);

        while (rs.next()) {
            Material material = new Material();
            material.setId(rs.getString("matnr"));
            material.setDescription(rs.getString("maktx"));
            materialList.add(material);
        }

        rs.close();
    }
}
