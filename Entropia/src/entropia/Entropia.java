package entropia;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;
/**
 *
 * @author Fabián Orduña Ferreira
 */
public class Entropia {

    public String obtenRutaDeArchivo(){
        String res = null;
        
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Selecciona el archivo para calcular su entropia: ");
        //jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile()!=null) {
                res = jfc.getSelectedFile().toPath().toString();
            }
        }
        return res;
    }
    
    public String obtenRutaDirectorio(){
        String res = null;
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Selecciona el lugar donde quieras guardar tu archivo: ");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setAcceptAllFileFilterUsed(false);
        int returnValue = jfc.showSaveDialog(null);
        //System.out.println(returnValue == JFileChooser.APPROVE_OPTION);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            res = jfc.getSelectedFile().toPath().toString();
        }
        return res;
    }
    
    public String recibeNombre(){
        return JOptionPane.showInputDialog(null, "Escribe un nombre de archivo para guardar los resultados");

    }
    
    public byte [] binarioDeArchivo(String rutaArchivo){
        Path path = Paths.get(rutaArchivo);
        try {  
            return Files.readAllBytes(path);
        } catch (Exception e) {}
        return null;
    }
    
    public String transformaABits(byte b) {
        String result = "";
        for(int i = 0; i < 8; i++)
            result += (b & (1 << i)) == 0 ? "0" : "1";
        return result;
    }
    
    public double[] agrupaBytes(byte [] arreglo){
        double [] bytes = new double[256];
        int entero;
        for (int i = 0; i < arreglo.length; i++) {
            entero = (int)arreglo[i] & 0xff;//porque el byte esta en complemento
            //System.out.println("Se transformarà: "+ entero );
            bytes[entero] ++;   
        }
        return bytes;
    }
    
    public double[] obtenProporciones(double [] arreglo1, int totalDeVeces){
        int len = arreglo1.length;
        double res [] = new double[len];
        for (int i = 0; i < arreglo1.length; i++) {
            res[i] = arreglo1[i] / totalDeVeces;
        }
        return res;
    }
    
    public double informacion(double probabilidad){
        return -1*logaritmoBaseX(probabilidad,2);
    }
    
    public double logaritmoBaseX(double numero, int base){
        double res = 0;
        if(numero!=0.0){
            res = Math.log(numero)/Math.log(base);
            //System.out.println("Logaritmo: "+res);
        }
        return res;
    }
    
    public double entropia(double [] probabilidades){
        double res = 0;
        double num=0;
        for (int i = 0; i < probabilidades.length; i++) {
            num = probabilidades[i];
            res = res + informacion(num)*num;
            //System.out.println("Res parcial: "+res);
        }
        return res;
    }
    
    public void imprimeArreglo(double [] arregloAImprimir){
        String cad = "";
        for (int i = 0; i < arregloAImprimir.length; i++) {
            cad = "Bits ["+i+"] ="+arregloAImprimir[i];
            System.out.println(cad);
        }
    }
    
    public void escribeEnArchivo(String rutaOriginal, String rutaDestino, double [] incidencias ,double [] probabilidades, double entropia) throws IOException{
        FileWriter archivo = new FileWriter(rutaDestino,false);
        PrintWriter pw = new PrintWriter(archivo);
            pw.printf("Ruta original ");
            pw.printf(rutaOriginal);
            pw.printf("\n ");
            pw.printf("Incidencias: ");pw.printf("\n ");
            String cad;
            for (int i = 0; i < incidencias.length; i++) {
                cad = "Bits ["+i+"] ="+incidencias[i];
                pw.printf(cad);pw.printf("\n ");
                //System.out.println(arreglo[i]);
            }
            pw.printf("\n ");
            pw.printf("Probabilidades: ");pw.printf("\n ");

            for (int i = 0; i < probabilidades.length; i++) {
                cad = "Bits ["+i+"] ="+probabilidades[i];
                pw.printf(cad);pw.printf("\n ");
                //System.out.println(arreglo[i]);
            }
            pw.printf("\n ");
            pw.printf("Entropia: ");
            cad = ""+entropia;
            pw.printf(cad);
            pw.printf("\n ");
        pw.close();
    }
    
    public void calculaLaEntropia(){
        try{
             String ruta = obtenRutaDeArchivo();
             if(ruta!=null){         
                System.out.println("Ruta del archivo "+ ruta);
                byte[] b = binarioDeArchivo(ruta);
                double arreglo [] = agrupaBytes(b);
                System.out.println("Incidencias:");
                imprimeArreglo(arreglo);
                double proporciones [] = obtenProporciones(arreglo, b.length);
                System.out.println("\nProbabilidades:");
                imprimeArreglo(proporciones);
                double entropia = entropia(proporciones);
                System.out.println("\nEntropia: "+entropia);
                String rutaDestino = obtenRutaDirectorio()+"/" +recibeNombre();
                //System.out.println(rutaDestino);
                escribeEnArchivo(ruta,  rutaDestino, arreglo, proporciones, entropia);
            }
            }catch(IOException e){
                JOptionPane.showMessageDialog(null, "Ocurrió un error "+ e.toString(), "InfoBox: " + "ERROR", JOptionPane.INFORMATION_MESSAGE);
            }
    }
    
    public static void main(String[] args) throws IOException {
        Entropia ent = new Entropia();
        int continuar = 0;
        //System.out.println(JOptionPane.NO_OPTION);
        while(continuar == 0){
            ent.calculaLaEntropia();
            continuar = JOptionPane.showConfirmDialog( null, "¿Quiere calcular otra entropia?",
                            "An Inane Question",
                            JOptionPane.YES_NO_OPTION);
        }
    } 
}