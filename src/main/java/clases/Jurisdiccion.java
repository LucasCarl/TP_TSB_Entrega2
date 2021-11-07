package clases;

public class Jurisdiccion
{
    private int id;
    private String nombre;

    public Jurisdiccion(String n, int i)
    {
        nombre = n;
        id = i;
    }

    @Override
    public String toString()
    {
        return nombre + " (ID: " + id + ")";
    }

    public String getNombre() {
        return nombre;
    }
}
