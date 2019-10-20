package randomwalkjava;

import org.jetbrains.annotations.Contract;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Jari Sunnari
 * jari.sunnari@gmail.com
 *
 * Class for checking and creating files and folders
 */
class FilesAndFolders {
    private String language;

    FilesAndFolders (String language){
        this.setLanguage(language);
    }

    /**
     * If data folder C:/RWDATA does not exist, tries to create it,
     * then tries to create source files
     * <p>
     *     Returns true if cannot create files or data folder
     * </p>
     * @param datapath data folder C:/RWDATA
     * @param fexec Fortran executable
     * @param pyexecrms Python executable for rms calculation plot
     * @param pyexec1d Python executable for path tracing plot in 1D
     * @param pyexec2d Python executable for path tracing plot in 2D
     * @param pyexec3d Python executable for path tracing plot in 3D
     * @param pyexecmmc2d Python executable for mmc diffusion plot in 2D
     * @param pyexecmmc3d Python executable for mmc diffusion plot in 3D
     * @param pyexec1Ddist Python executable for 1d distance plot
     * @param pyexecsaw2d Python executable for saw plot in 2D
     * @param pyexecsaw3d Python executable for saw plot in 3D
     * @return true in error, false otherwise
     */
    boolean checkCreateFolders(String datapath, String fexec, String pyexecrms, String pyexec1d,
                            String pyexec2d, String pyexec3d, String pyexecmmc2d, String pyexecmmc3d,
                            String pyexec1Ddist, String pyexecsaw2d, String pyexecsaw3d){

        if (createFolder(datapath, fexec, true)) return true;
        if (createFolder(datapath, pyexecrms, false)) return true;
        if (createFolder(datapath, pyexec1d, false)) return true;
        if (createFolder(datapath, pyexec2d, false)) return true;
        if (createFolder(datapath, pyexec3d, false)) return true;
        if (createFolder(datapath, pyexecmmc2d, false)) return true;
        if (createFolder(datapath, pyexecmmc3d, false)) return true;
        if (createFolder(datapath, pyexec1Ddist, false)) return true;
        if (createFolder(datapath, pyexecsaw2d, false)) return true;
        return createFolder(datapath, pyexecsaw3d, false);
    }

    /**
     * If data folder C:/RWDATA does exist, tries to create source files
     * <p>
     *     Returns true if cannot create files
     * </p>
     * @param datapath data folder C:/RWDATA
     * @param pyexecrms Python executable for rms calculation plot
     * @param pyexec1d Python executable for path tracing plot in 1D
     * @param pyexec2d Python executable for path tracing plot in 2D
     * @param pyexec3d Python executable for path tracing plot in 3D
     * @param pyexecmmc2d Python executable for mmc diffusion plot in 2D
     * @param pyexecmmc3d Python executable for mmc diffusion plot in 3D
     * @param pyexec1Ddist Python executable for 1d distance plot
     * @param pyexecsaw2d Python executable for saw plot in 2D
     * @param pyexecsaw3d Python executable for saw plot in 3D
     * @return true in error, false otherwise
     */
    boolean checkSourceFiles(String datapath, String pyexecrms, String pyexec1d,
                             String pyexec2d, String pyexec3d, String pyexecmmc2d, String pyexecmmc3d,
                             String pyexec1Ddist, String pyexecsaw2d, String pyexecsaw3d) {

        File sourceFile = new File(datapath + "/" + pyexecrms);
        if (Files.notExists(sourceFile.toPath())) return createFolder(datapath, pyexecrms, false);

        sourceFile = new File(datapath + "/" + pyexec1d);
        if (Files.notExists(sourceFile.toPath())) return createFolder(datapath, pyexec1d, false);

        sourceFile = new File(datapath + "/" + pyexec2d);
        if (Files.notExists(sourceFile.toPath())) return createFolder(datapath, pyexec2d, false);

        sourceFile = new File(datapath + "/" + pyexec3d);
        if (Files.notExists(sourceFile.toPath())) return createFolder(datapath, pyexec3d, false);

        sourceFile = new File(datapath + "/" + pyexecmmc2d);
        if (Files.notExists(sourceFile.toPath())) return createFolder(datapath, pyexecmmc2d, false);

        sourceFile = new File(datapath + "/" + pyexecmmc3d);
        if (Files.notExists(sourceFile.toPath())) return createFolder(datapath, pyexecmmc3d, false);

        sourceFile = new File(datapath + "/" + pyexec1Ddist);
        if (Files.notExists(sourceFile.toPath())) return createFolder(datapath, pyexec1Ddist, false);

        sourceFile = new File(datapath + "/" + pyexecsaw2d);
        if (Files.notExists(sourceFile.toPath())) return createFolder(datapath, pyexecsaw2d, false);

        sourceFile = new File(datapath + "/" + pyexecsaw3d);
        if (Files.notExists(sourceFile.toPath())) return createFolder(datapath, pyexecsaw3d, false);

        return false;
    }

    /**
     * method for creating a working directory C:/RWDATA if needed, and
     * copies executables there from resources/.
     * <p>
     *     returns false if all goes well, true otherwise
     * </p>
     * @param destination path for working directory C:/RWDATA
     * @param executable file to copy from resources/ to C:/RWDATA
     * @param createDir true if has to create working directory
     * @return false if all goes well, true otherwise
     */
    boolean createFolder(String destination, String executable, boolean createDir){

        File dataFolder = new File(destination);
        Path dataPath = dataFolder.toPath();

        if ( createDir ) {
            if(!Files.exists(dataPath)) {
                try {
                    Files.createDirectory(dataPath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return true;
                }
                System.out.println(this.getLanguage().equals("fin") ? "luodaan tiedostopolku: " + destination : "creating directory: " + destination);
            }
            else System.out.println(this.getLanguage().equals("fin") ? "Ei voitu luoda uutta tiedostopolkua.\n" : "Could not create a new directory.\n");
        }

        File destinationFile = new File(destination + "/" + executable);
        boolean createdNewFile;
        try {
            createdNewFile = destinationFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

        InputStream fin = null;
        OutputStream fout = null;

        if (createdNewFile) {
            try {
                String infotext = this.getLanguage().equals("fin")
                    ? "Kopioidaan resurssitiedosto '" + executable + "' kansioon '" + destination + "', odota hetki..."
                    : "Copying resource file '" + executable + "' into folder '" + destination + "', please wait...";
                System.out.println(infotext);
                fin = new BufferedInputStream(RandomWalk.class.getResourceAsStream("/"+executable));
                fout = new BufferedOutputStream(new FileOutputStream(destinationFile, false));
                byte[] buffer = new byte[1024];
                int read;
                while ((read = fin.read(buffer)) != -1) {
                    fout.write(buffer, 0, read);
                }
                System.out.println(this.getLanguage().equals("fin") ? "Kopiointi suoritettu." : "Copying finished.");
            } catch (IOException e) {
                String infotext = this.getLanguage().equals("fin")
                    ? "Resurssitiedostoa '" + executable + "' ei kopioitu kansioon '" + destination + "'.\n" + e.getMessage()
                    : "Resource file '" + executable + "' not copied into folder '" + destination + "'.\n" + e.getMessage();
                System.out.println(infotext);
                return true;
            } finally {
                try {
                    if (fin != null) fin.close();
                    if (fout != null) fout.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

        }
        return false;
    }

    /**
     * @return the language
     */
    @Contract(pure = true)
    private String getLanguage() { return this.language; }

    /**
     * @param language the language to set
     */
    private void setLanguage(String language) { this.language = language; }

}
