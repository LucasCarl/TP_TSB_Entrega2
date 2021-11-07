package clases;

import java.util.List;
import java.util.ArrayList;

public class Contador
{
    private int contMasculino, contFemenino, contOtro;
    private int contPrimera, contSegunda, contExtra;
    private List<Integer> contVacunas;
    private List<String> nombresVacunas;

    public Contador()
    {
        contMasculino = 0;
        contFemenino = 0;
        contOtro = 0;
        contPrimera = 0;
        contSegunda = 0;
        contExtra = 0;
        contVacunas = new ArrayList<>();
        nombresVacunas = new ArrayList<>();
    }

    public void contarSexo(String sexo)
    {
        switch (sexo)
        {
            case "M":
                contMasculino++;
                break;

            case "F":
                contFemenino++;
                break;

            default:
                contOtro++;
                break;
        }
    }

    public void contarOrden(String orden)
    {
        switch (orden)
        {
            case "1":
                contPrimera++;
                break;

            case "2":
                contSegunda++;
                break;

            default:
                contExtra++;
                break;
        }
    }

    public void contarVacuna(String vacuna)
    {
        //Busca si esta la vacuna ya registrada y suma
        for (int i = 0; i < nombresVacunas.size(); i++)
        {
            if(nombresVacunas.get(i).equals(vacuna))
            {
                contVacunas.set(i, contVacunas.get(i)+1);
                return;
            }
        }

        //Agrega la nueva vacuna a la lista
        nombresVacunas.add(vacuna);
        contVacunas.add(1);
    }

    public int getContMasculino() {
        return contMasculino;
    }
    public int getContFemenino() {
        return contFemenino;
    }
    public int getContOtro() {
        return contOtro;
    }

    public int getContPrimera() {
        return contPrimera;
    }
    public int getContSegunda() {
        return contSegunda;
    }
    public int getContExtra() {
        return contExtra;
    }

    public int[] getContVacunas() {
        int[] resultado = new int[contVacunas.size()];
        for (int i = 0; i < resultado.length; i++)
        {
            resultado[i] = contVacunas.get(i);
        }
        return resultado;
    }

    public String[] getNombresVacunas() {
        String[] resultado = new String[contVacunas.size()];
        for (int i = 0; i < resultado.length; i++)
        {
            resultado[i] = nombresVacunas.get(i);
        }
        return resultado;
    }
}
