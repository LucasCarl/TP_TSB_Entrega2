package clases;

public class Departamento
{
    private int id;
    private String nombre;

    public Departamento(String n, int i)
    {
        nombre = n;
        id = i;
    }

    @Override
    public String toString()
    {
        return nombre + " (ID: " + id + ")";
    }
}
