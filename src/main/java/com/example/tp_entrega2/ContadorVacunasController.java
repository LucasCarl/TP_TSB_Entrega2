package com.example.tp_entrega2;

import clases.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ContadorVacunasController
{
    @FXML
    Label lblArchivoNombre;
    @FXML
    Label lblProcesando;
    @FXML
    Button btnElegirArchivo;
    @FXML
    TableView<DatoTabla> tblDatos;
    @FXML
    ComboBox<String> cbxDepartamentos;
    @FXML
    RadioButton rdbSexo;
    @FXML
    RadioButton rdbOrden;
    @FXML
    RadioButton rdbVacuna;

    //Crear tabla hash
    TSBHashTableDA<String, Contador> tablaHashDepartamentos = new TSBHashTableDA<String, Contador>(101);
    Contador contadorActual;

    @FXML
    void BtnElegirArchivoClick()
    {
        //Pone texto procesando para que no parezca que se clavo el programa
        lblProcesando.setText("Procesando...");
        ///Busqueda del archivo
        FileChooser archChooser = new FileChooser();
        FileChooser.ExtensionFilter filtro = new FileChooser.ExtensionFilter("Archivos de Valores Separados por Comas", "*.csv");
        archChooser.getExtensionFilters().add(filtro);
        archChooser.setTitle("Elegir archivo");
        File archivo = archChooser.showOpenDialog(null);
        //Mostrar nombre del archivo
        lblArchivoNombre.setText("Archivo: " + archivo.getName());

        ///Lectura del archivo
        try
        {
            Scanner lectorArchivo = new Scanner(archivo);
            lectorArchivo.nextLine();   //Se saltea la primera linea que son los titulos de las columnas
            tablaHashDepartamentos.put("Todos", new Contador());
            List<String> nombreDepartamentos = new ArrayList<String>();
            int n = 0;

            while (lectorArchivo.hasNextLine())
            {
                //Tomar una linea del arhivo y crea Scanner para analizarla
                String datoCrudo = lectorArchivo.nextLine();
                Dato dato = new Dato(datoCrudo);
                //Analiza el dato y suma al contador total
                if(dato.getJurisdiccionAplicacion().getNombre().equals("Córdoba"))
                {
                    //Conteo total
                    relizarConteos(tablaHashDepartamentos, "Todos", dato);

                    //Contador de departamento
                    String dptoNombre = dato.getDeptoAplicacion().getNombre();

                    //Si no existe el departamento en la tabla lo agrega
                    if(!tablaHashDepartamentos.containsKey(dptoNombre))
                    {
                        Contador contadorDpto = new Contador();
                        tablaHashDepartamentos.put(dptoNombre, contadorDpto);
                        nombreDepartamentos.add(dptoNombre);
                    }

                    //Conteo Departamento
                    relizarConteos(tablaHashDepartamentos, dptoNombre, dato);
                }
            }

            System.out.println("Lectura Terminada!");

            //Agrego nombres departamentos a combobox
            cbxDepartamentos.getItems().clear();
            Collections.sort(nombreDepartamentos);
            nombreDepartamentos.add(0, "Todos");
            cbxDepartamentos.getItems().addAll(nombreDepartamentos);
        }
        catch (FileNotFoundException exNoEncontrado)
        {
            System.err.println("Error inesperado, archivo no encontrado");
        }

        //Habilita el combobox
        cbxDepartamentos.setDisable(false);
        //Borra texto procesando
        lblProcesando.setText("");
    }

    private void relizarConteos(TSBHashTableDA<String, Contador> tabla, String contadorNombre, Dato dato)
    {
        Contador cont = tabla.get(contadorNombre);
        cont.contarSexo(dato.getSexo());
        cont.contarOrden("" + dato.getOrdenDosis());
        cont.contarVacuna(dato.getVacuna());
    }

    @FXML
    void cbxDepartamentosClick()
    {
        //Toma el contador del departamento elegido
        String dpto = cbxDepartamentos.getValue();
        contadorActual = tablaHashDepartamentos.get(dpto);
        //Limpia la tabla
        tblDatos.getItems().clear();
        tblDatos.getColumns().clear();

        //Habilitar los radiobuttons
        rdbSexo.setDisable(false);
        rdbOrden.setDisable(false);
        rdbVacuna.setDisable(false);
        //Resetea los radiobuttons
        rdbSexo.setSelected(false);
        rdbOrden.setSelected(false);
        rdbVacuna.setSelected(false);

        System.out.println("\nConteo: ");
        System.out.println("Masculinos: " + contadorActual.getContMasculino() + " -- Femeninos: " + contadorActual.getContFemenino() + " -- Otros: " + contadorActual.getContOtro());
        System.out.println("Primera: " + contadorActual.getContPrimera() + " -- Segunda: " + contadorActual.getContSegunda() + " -- Extra: " + contadorActual.getContExtra());
        int[] contVacunas = contadorActual.getContVacunas();
        String[] nombresVacunas = contadorActual.getNombresVacunas();
        for (int i = 0; i < nombresVacunas.length; i++) {
            System.out.print(nombresVacunas[i] + ": " + contVacunas[i] + " -- ");
        }
    }

    @FXML
    void rdbSexoClick()
    {
        //Limpia la tabla
        tblDatos.getItems().clear();
        tblDatos.getColumns().clear();

        //Crea las columnas
        TableColumn<DatoTabla, String> colDescripcion = new TableColumn<>("Sexo");
        TableColumn<DatoTabla, Integer> colConteo = new TableColumn<>("Conteo");
        colDescripcion.setPrefWidth(210);
        colConteo.setPrefWidth(210);
        colDescripcion.setCellValueFactory(new PropertyValueFactory<DatoTabla, String>("descripcion"));
        colConteo.setCellValueFactory(new PropertyValueFactory<DatoTabla,Integer>("cantidad"));

        //Agrega los datos
        tblDatos.getColumns().addAll(colDescripcion, colConteo);
        tblDatos.getItems().add(new DatoTabla("Masculino", contadorActual.getContMasculino()));
        tblDatos.getItems().add(new DatoTabla("Femenino", contadorActual.getContFemenino()));

    }
    @FXML
    void rdbOrdenClick()
    {
        //Limpia la tabla
        tblDatos.getItems().clear();
        tblDatos.getColumns().clear();

        //Crea las columnas
        TableColumn<DatoTabla, String> colDescripcion = new TableColumn<>("Orden Dosis");
        TableColumn<DatoTabla, Integer> colConteo = new TableColumn<>("Conteo");
        colDescripcion.setPrefWidth(210);
        colConteo.setPrefWidth(210);
        colDescripcion.setCellValueFactory(new PropertyValueFactory<DatoTabla, String>("descripcion"));
        colConteo.setCellValueFactory(new PropertyValueFactory<DatoTabla,Integer>("cantidad"));

        //Agrega los datos
        tblDatos.getColumns().addAll(colDescripcion, colConteo);
        tblDatos.getItems().add(new DatoTabla("Primera Dosis", contadorActual.getContPrimera()));
        tblDatos.getItems().add(new DatoTabla("Segunda Dosis", contadorActual.getContSegunda()));
        tblDatos.getItems().add(new DatoTabla("Dosis Extra", contadorActual.getContExtra()));
    }
    @FXML
    void rdbVacunaClick()
    {
        //Limpia la tabla
        tblDatos.getItems().clear();
        tblDatos.getColumns().clear();

        //Crea las columnas
        TableColumn<DatoTabla, String> colDescripcion = new TableColumn<>("Vacuna");
        TableColumn<DatoTabla, Integer> colConteo = new TableColumn<>("Conteo");
        colDescripcion.setPrefWidth(210);
        colConteo.setPrefWidth(210);
        colDescripcion.setCellValueFactory(new PropertyValueFactory<DatoTabla, String>("descripcion"));
        colConteo.setCellValueFactory(new PropertyValueFactory<DatoTabla,Integer>("cantidad"));

        //Agrega los datos
        tblDatos.getColumns().addAll(colDescripcion, colConteo);
        int[] cantidadesVacunas = contadorActual.getContVacunas();
        String[] nombresVacunas = contadorActual.getNombresVacunas();

        for (int i = 0; i < cantidadesVacunas.length; i++)
        {
            tblDatos.getItems().add(new DatoTabla(nombresVacunas[i], cantidadesVacunas[i]));
        }
    }

    public class DatoTabla
    {
        String descripcion;
        int cantidad;

        public DatoTabla(String desc, int cant)
        {
            descripcion = desc;
            cantidad = cant;
        }

        public int getCantidad() {
            return cantidad;
        }
        public String getDescripcion() {
            return descripcion;
        }
    }
}