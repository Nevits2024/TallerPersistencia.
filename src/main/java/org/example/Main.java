package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String URL = "jdbc:sqlite:productos.db";

    public static void main(String[] args) {
        crearTabla();
        Scanner sc = new Scanner(System.in);
        int op = 0;

        do {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Registrar | 2. Consultar | 3. Actualizar | 4. Eliminar | 5. Salir");
            System.out.print("Seleccione una opción: ");
            try { op = Integer.parseInt(sc.nextLine()); } catch (Exception e) { op = 0; }

            switch (op) {
                case 1 -> registrar(sc);
                case 2 -> listar(sc);
                case 3 -> actualizar(sc);
                case 4 -> eliminar(sc);
            }
        } while (op != 5);
        sc.close();
    }

    public static void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS productos (" +
                "codigo TEXT PRIMARY KEY, " +
                "nombre TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "precio REAL NOT NULL, " +
                "cantidad INTEGER NOT NULL)";
        try (Connection c = DriverManager.getConnection(URL); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    public static void registrar(Scanner sc) {
        System.out.print("Código: "); String cod = sc.nextLine();
        System.out.print("Nombre: "); String nom = sc.nextLine();
        System.out.print("Descripción: "); String desc = sc.nextLine();
        try {
            System.out.print("Precio: "); double pre = Double.parseDouble(sc.nextLine());
            System.out.print("Cantidad: "); int cant = Integer.parseInt(sc.nextLine());

            if (pre < 0 || cant < 0) { System.out.println("Error: No se permiten valores negativos."); return; }

            String sql = "INSERT INTO productos (codigo, nombre, descripcion, precio, cantidad) VALUES (?, ?, ?, ?, ?)";
            try (Connection c = DriverManager.getConnection(URL); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, cod);
                ps.setString(2, nom);
                ps.setString(3, desc);
                ps.setDouble(4, pre);
                ps.setInt(5, cant);
                ps.executeUpdate();
                System.out.println("¡Registrado con éxito!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar valores numéricos válidos en precio y cantidad.");
        } catch (SQLException e) {
            System.out.println("Error al registrar (¿Código repetido?): " + e.getMessage());
        }
    }

    public static void listar(Scanner sc) {
        System.out.print("¿Buscar por código específico? (s/n): ");
        String r = sc.nextLine().trim();

        if (r.equalsIgnoreCase("s")) {
            System.out.print("Ingresa el código: "); String cod = sc.nextLine();
            String sql = "SELECT * FROM productos WHERE codigo = ?";
            try (Connection c = DriverManager.getConnection(URL); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, cod);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println(rs.getString("codigo") + " | " + rs.getString("nombre") + " | " + rs.getString("descripcion") + " | $" + rs.getDouble("precio") + " | Stock: " + rs.getInt("cantidad"));
                } else {
                    System.out.println("No existe un producto con ese código.");
                }
            } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
        } else if (r.equalsIgnoreCase("n")) {
            String sql = "SELECT * FROM productos";
            try (Connection c = DriverManager.getConnection(URL); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
                System.out.println("\n--- INVENTARIO ---");
                while (rs.next()) {
                    System.out.println(rs.getString("codigo") + " | " + rs.getString("nombre") + " | " + rs.getString("descripcion") + " | $" + rs.getDouble("precio") + " | Stock: " + rs.getInt("cantidad"));
                }
            } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
        } else {
            System.out.println("Opción inválida. Debe ingresar 's' o 'n'.");
        }
    }

    public static void actualizar(Scanner sc) {
        System.out.print("Código a actualizar: "); String cod = sc.nextLine();
        System.out.print("Nueva descripción: "); String desc = sc.nextLine();
        try {
            System.out.print("Nuevo precio: "); double pre = Double.parseDouble(sc.nextLine());
            System.out.print("Nueva cantidad: "); int cant = Integer.parseInt(sc.nextLine());

            if (pre < 0 || cant < 0) { System.out.println("Error: Valores no pueden ser negativos."); return; }

            String sql = "UPDATE productos SET descripcion = ?, precio = ?, cantidad = ? WHERE codigo = ?";
            try (Connection c = DriverManager.getConnection(URL); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, desc);
                ps.setDouble(2, pre);
                ps.setInt(3, cant);
                ps.setString(4, cod);
                if (ps.executeUpdate() > 0) System.out.println("¡Actualizado con éxito!");
                else System.out.println("Producto no encontrado.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese números válidos.");
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    public static void eliminar(Scanner sc) {
        System.out.print("Código a eliminar: "); String cod = sc.nextLine();
        String sql = "DELETE FROM productos WHERE codigo = ?";
        try (Connection c = DriverManager.getConnection(URL); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cod);
            if (ps.executeUpdate() > 0) System.out.println("¡Eliminado con éxito!");
            else System.out.println("Producto no encontrado.");
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }
}