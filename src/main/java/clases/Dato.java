package clases;

import java.util.Scanner;

public class Dato
{
    private String sexo;
    private String grupoEtario;
    private Jurisdiccion jurisdiccionResidencia;
    private Departamento deptoResidencia;
    private Jurisdiccion jurisdiccionAplicacion;
    private Departamento deptoAplicacion;
    private String fechaAplicacion;
    private String vacuna;
    private String condicionAplicacion;
    private int ordenDosis;
    private String loteVacuna;

    public Dato(String datoCrudo)
    {
        Scanner lectorDato = new Scanner(datoCrudo);
        lectorDato.useDelimiter(",");

        //Lee los datos
        String sexoStrg = lectorDato.next();
        sexo = sexoStrg.substring(1, sexoStrg.length()-1);

        String grupoEtarioStrg = lectorDato.next();
        grupoEtario = grupoEtarioStrg.substring(1, grupoEtarioStrg.length()-1);

        String jurisResNomStrg = lectorDato.next();
        String jurisResIdStrg = lectorDato.next();
        String nombreJurR = jurisResNomStrg.substring(1, jurisResNomStrg.length()-1);
        int idJurR = Integer.parseInt(jurisResIdStrg.substring(1, jurisResIdStrg.length()-1));
        jurisdiccionResidencia = new Jurisdiccion(nombreJurR, idJurR);

        String dptoResNomStrg = lectorDato.next();
        String dptoResIdStrg = lectorDato.next();
        String nombreDptoR = dptoResNomStrg.substring(1, dptoResNomStrg.length()-1);
        int idDptoR = Integer.parseInt(dptoResIdStrg.substring(1, dptoResIdStrg.length()-1));
        deptoResidencia = new Departamento(nombreDptoR, idDptoR);

        String jurisAplNomStrg = lectorDato.next();
        String jurisAplIdStrg = lectorDato.next();
        String nombreJurA = jurisAplNomStrg.substring(1, jurisAplNomStrg.length()-1);
        int idJurA = Integer.parseInt(jurisAplIdStrg.substring(1, jurisAplIdStrg.length()-1));
        jurisdiccionAplicacion = new Jurisdiccion(nombreJurA, idJurA);

        String dptoAplNomStrg = lectorDato.next();
        String dptoAplIdStrg = lectorDato.next();
        String nombreDptoA = dptoAplNomStrg.substring(1, dptoAplNomStrg.length()-1);
        int idDptoA = Integer.parseInt(dptoAplIdStrg.substring(1, dptoAplIdStrg.length()-1));
        deptoAplicacion = new Departamento(nombreDptoA, idDptoA);

        String fechaStrg = lectorDato.next();
        fechaAplicacion = fechaStrg.substring(1, fechaStrg.length()-1);

        String vacunaStrg = lectorDato.next();
        vacuna = vacunaStrg.substring(1, vacunaStrg.length()-1);

        String condicionStrg = lectorDato.next();
        condicionAplicacion = condicionStrg.substring(1, condicionStrg.length()-1);

        String ordenStrg = lectorDato.next();
        ordenDosis = Integer.parseInt(ordenStrg);   //No substring porque no tiene comillas cuando toma el dato

        String loteStrg = lectorDato.next();
        loteVacuna = loteStrg.substring(1, loteStrg.length()-1);
    }

    @Override
    public String toString()
    {
        return  ("Dato--> Sexo: " + sexo + " - Grupo Etario: " + grupoEtario + " - Jurisdiccion de Residencia: " + jurisdiccionResidencia.toString() +
                " - Departamento de Residencia: " + deptoResidencia.toString() + " - Jurisdiccion de Aplicacion: " + jurisdiccionAplicacion.toString() +
                " - Departamento de Aplicacion: " + deptoAplicacion.toString() + " - Fecha de Aplicacion: " + fechaAplicacion + " - Vacuna: " + vacuna +
                " - Condicion de Aplicacion: " + condicionAplicacion + " - Orden de Dosis: " + ordenDosis + " - Lote de Vacuna: " + loteVacuna);

    }

    public String getSexo() {
        return sexo;
    }
    public String getGrupoEtario() {
        return grupoEtario;
    }
    public Jurisdiccion getJurisdiccionResidencia() {
        return jurisdiccionResidencia;
    }
    public Departamento getDeptoResidencia() {
        return deptoResidencia;
    }
    public Jurisdiccion getJurisdiccionAplicacion() {
        return jurisdiccionAplicacion;
    }
    public Departamento getDeptoAplicacion() {
        return deptoAplicacion;
    }
    public String getFechaAplicacion() {
        return fechaAplicacion;
    }
    public String getVacuna() {
        return vacuna;
    }
    public String getCondicionAplicacion() {
        return condicionAplicacion;
    }
    public int getOrdenDosis() {
        return ordenDosis;
    }
    public String getLoteVacuna() {
        return loteVacuna;
    }
}
