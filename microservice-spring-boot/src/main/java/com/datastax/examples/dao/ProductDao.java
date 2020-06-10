package com.datastax.examples.dao;

import com.datastax.examples.model.Product;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductDao {

    private PreparedStatement insertProduct;
    private PreparedStatement selectProductByName;
    private PreparedStatement selectProductByNameAndId;
    private PreparedStatement deleteProductByName;
    private PreparedStatement deleteProductByNameAndId;
    private CqlSession session;

    private static final String productsTableName = "products";

    public ProductDao(CqlSession session, String keyspace, String localDataCenter, String isAstra){
        this.session = session;
        maybeCreateProductSchema(keyspace, localDataCenter, isAstra);
        this.insertProduct = session.prepare(getInsertProductStmt(keyspace));
        this.selectProductByName = session.prepare(getSelectProductByNameStmt(keyspace));
        this.selectProductByNameAndId = session.prepare(getSelectProductByNameAndIdStmt(keyspace));
        this.deleteProductByName = session.prepare(getDeleteProductByNameStmt(keyspace));
        this.deleteProductByNameAndId = session.prepare(getSelectProductByNameAndIdStmt(keyspace));
    }

    private void maybeCreateProductSchema(String keyspace, String localDataCenter, String isAstra){
        if (isAstra.equals("none")){
            session.execute(String.format("" +
                    "CREATE KEYSPACE IF NOT EXISTS %s WITH REPLICATION = {'class': 'NetworkTopologyStrategy', '%s': 1};",
                    keyspace, localDataCenter));
        }
        session.execute(String.format("CREATE TABLE IF NOT EXISTS %s.%s (name text, id uuid, description text, " +
                "price decimal, last_updated timestamp, PRIMARY KEY ((name), id));", keyspace, productsTableName));
    }

    private String getInsertProductStmt(String keyspace){
        return String.format("" +
                "INSERT INTO %s.%s (name, id, description, price, last_updated) " +
                "VALUES (?,?,?,?,toTimestamp(now()));", keyspace, productsTableName);
    }

    private String getSelectProductByNameStmt(String keyspace){
        return String.format("SELECT id, description, price, last_updated FROM %s.%s WHERE name=?;",
                keyspace, productsTableName);
    }

    private String getSelectProductByNameAndIdStmt(String keyspace){
        return String.format("SELECT description, price, last_updated FROM %s.%s WHERE name=? AND id=?;",
                keyspace, productsTableName);
    }

    private String getDeleteProductByNameStmt(String keyspace){
        return String.format("DELETE FROM %s.%s WHERE name=?;",
                keyspace, productsTableName);
    }

    private String getDeleteProductByNameAndIdStmt(String keyspace){
        return String.format("DELETE FROM %s.%s WHERE name=? AND id=?;",
                keyspace, productsTableName);
    }

    public Iterable<Product> findByName(String name){
        ResultSet rs = session.execute(selectProductByName.bind(name));
        List<Product> products = new ArrayList<>(rs.getAvailableWithoutFetching());
        for (Row row : rs){
            products.add(new Product(name, row.getUuid("id"), row.getString("description"), row.getBigDecimal("price"), row.getInstant("last_updated")));
        }
        return products;
    }

    public Product findByNameAndId(String name, UUID id){
        ResultSet rs = session.execute(selectProductByNameAndId.bind(name, id));
        Row row = rs.one();
        return new Product(name, id, row.getString("description"), row.getBigDecimal("price"), row.getInstant("last_updated"));
    }

    public void addProduct(Product product){
        session.execute(insertProduct.bind(product.getName(), product.getId(), product.getDescription(), product.getPrice()));
    }

    public void deleteByName(String name){
        session.execute(deleteProductByName.bind(name));
    }

    public void deleteByNameAndId(String name, UUID id){
        session.execute(deleteProductByNameAndId.bind(name, id));
    }
}
