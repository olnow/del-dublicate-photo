package olnow.del_dublicate_photo;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.nio.file.*;
import org.apache.commons.io.FileUtils;


public class delphoto {
    String from_photo_path = "C:\\Programs\\Projects\\IdeaProjects\\TestPhoto";
    String photo_path = "C:\\Programs\\Projects\\IdeaProjects\\TestPhoto2";
    String copyTo = "";
    boolean debug = true;

    public void log(String st) {

    }

    public void addRow(String src, String src_file, String dst, String dst_file) {

    }

    public void set_paths(String src, String dst) {
        from_photo_path = src;
        photo_path = dst;
    }


    public void setCopyTo(String copyTo) {
        this.copyTo = copyTo;
    }

    public ArrayList<File> get_folders() {
        File source_photo_dir = new File(from_photo_path);
        ArrayList<File> folders = new ArrayList<>();
        if (source_photo_dir.isDirectory()) {
            for (File item : source_photo_dir.listFiles()) {
                if (item.isDirectory())
                    folders.add(item);
            }
        }
        if (!folders.isEmpty())
            return folders;
        else return null;
    }

    private class myFileVisitor extends SimpleFileVisitor<Path> {
        int find_files = 0;
        String file_name_woext, dst_file;

        public int getFilesCount () {
            return find_files;
        }

        public String getFileName() {
            return dst_file;
        }

        public myFileVisitor(String file_name_woext) {
            this.file_name_woext = file_name_woext;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
            try {
                //log(file.getFileName().toString());
                if (file.getFileName().toString().indexOf(file_name_woext) == 0) {
                    //log("Find file: src - " + file_name_woext + ", dst - " + file.getFileName().toString());
                    find_files++;
                    dst_file = file.getFileName().toString();
                    return FileVisitResult.TERMINATE;
                }
            } catch (Exception e) {
                log(e.toString());
            }
            return FileVisitResult.CONTINUE;
        }
    }


    /*public void compare_folders(ArrayList<File> src_folders) {
        if (src_folders == null) return;
        for (File item : src_folders) {
            try {
                log("Start compare: " + item.toString());
                String folder_name = item.getName();
                if (debug) log("Folder:" + folder_name);
                File dst_folder = new File(photo_path + "\\" + folder_name + "*");
                if (debug) log("DST Folder" + dst_folder.toString());
                File[] same_files = dst_folder.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        try {
                            if (debug) log(dir.toString());
                            if (debug) log(name);
                        } catch (Exception e) {
                            log(e.toString());
                        }
                        return true;
                    }
                });
                if (debug) log("RES folder:" + same_files.toString());
            } catch (Exception e) {
                log(e.toString());
            }
        }
    }
    */

    public void compare_folders(String src_folder_name, String dst_path) {
        try {
            log("src_folder: " + src_folder_name);
            Path dst = Paths.get(dst_path);
            Files.walkFileTree(dst, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    try {
                        if (dir.compareTo(dst) == 0)
                            return FileVisitResult.CONTINUE;
                        log("Visitor Dir name: ");
                        log(dir.toString());
                        if (dir.toString().indexOf(src_folder_name) > 0) {
                            log("Find");
                        }
                    }
                    catch (Exception e) {
                        log(e.toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (Exception e) {
            log(e.toString());
        }

    }

    public void setProgress(double progr) {
    }

    public void compare_folders(ArrayList<File> src_folders, boolean copy, boolean delete) {
        if (src_folders == null) return;
        double progress = 0;
        double progress_step = (double) 1 / (double) src_folders.size();
        log("progress: " + progress_step);
        for (File item : src_folders) {
            try {
                Path dst = Paths.get(photo_path);
                Files.walkFileTree(dst, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        try {
                            if (dir.compareTo(dst) == 0)
                                return FileVisitResult.CONTINUE;
                            if (dir.compareTo(Paths.get(from_photo_path)) == 0)
                                return FileVisitResult.SKIP_SUBTREE;
                            if (dir.toString().indexOf(item.getName()) > 0) {
                                //log("Find: src - " + item.getName() + ", dst - " + dir.toString());
                                addRow(item.getPath(),"",dir.toString(),"");
                                int count = compare_files(Paths.get(item.getPath()) , dir);
                                if (count > 0 && copy) {
                                    //dir.getFileName().toString().substring(item.getName().length())
                                    FileUtils.copyDirectory(item, new File(copyTo + "\\" + item.getName() + dir.getFileName().toString().substring(item.getName().length())));
                                    if (delete)
                                        FileUtils.deleteDirectory(item);
                                    //Files.copy(item.toPath(), Paths.get(copyTo), StandardCopyOption.REPLACE_EXISTING);
                                    log("Copy" + (delete ? " and Delete": "") + ": "  + item.getPath());
                                }
                                //    addRow("","", "", Long.toString(Files.size(dir)));

                            }
                        } catch (Exception e) {
                            log(e.toString());
                        }
                        return FileVisitResult.CONTINUE;
                    }

                });
                //Change progress
                progress += progress_step;
                setProgress(progress);
                log("Progress %: " + progress);
            } catch (Exception e) {
                log(e.toString());
            }
        }
    }


    public int compare_files(Path src, Path dst) {
        if (src == null || dst == null) return 0;
        int res = 0;
        File src_files = new File(src.toString());
        if (src_files.isDirectory()) {
            for (File item : src_files.listFiles()) {
                //log("Compare files: " + item.getName());
                if (item.isFile()) {
                    String file_name = item.getName();
                    String file_name_woext = file_name.substring(0,file_name.indexOf("."));
                    try {
                        myFileVisitor find_files = new myFileVisitor(file_name_woext);
                        Files.walkFileTree(dst, find_files);
                        res = find_files.getFilesCount();
                        if (res > 0) {
                            addRow("",file_name, find_files.getFileName(), Integer.toString(res));
                            return find_files.getFilesCount();
                        }

                    }
                    catch (Exception e) {
                        log(e.toString());
                    }

                }
            }
        }
        return res;
    }

}
