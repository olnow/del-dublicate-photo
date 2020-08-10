package olnow.del_dublicate_photo;

import olnow.del_dublicate_photo.delphoto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Controller {
    @FXML private TextArea mText;
    @FXML private TextField mTextFieldSrc, mTextFieldDst, mTextCopyDst;
    @FXML private ProgressBar mProgressBar;
    @FXML private TableView<Row> mTableView;
    @FXML private TableColumn<Row, String> tbv_src, tbv_src_file, tbv_dst, tbv_dst_file;
    private ObservableList<Row> tableData = FXCollections.observableArrayList();
    String from_photo_path = "C:\\Programs\\Projects\\IdeaProjects\\TestPhoto";
    String photo_path = "C:\\Programs\\Projects\\IdeaProjects\\TestPhoto2";

    public class Row {
      private String src;
      private String src_file;
      private String dst;
      private String dst_file;

      public Row (String src, String src_file, String dst, String dst_file) {
          this.src = src;
          this.src_file = src_file;
          this.dst = dst;
          this.dst_file = dst_file;
      }

        public String getDst() {
            return dst;
        }

        public String getSrc() {
            return src;
        }

        public String getSrc_file() {
            return src_file;
        }

        public String getDst_file() {
            return dst_file;
        }
    }


    public void initialize () {
        mTextFieldSrc.setText(from_photo_path);
        mTextFieldDst.setText(photo_path);

        tbv_src.setCellValueFactory(new PropertyValueFactory<Row, String>("src"));
        tbv_src_file.setCellValueFactory(new PropertyValueFactory<Row, String>("src_file"));
        tbv_dst.setCellValueFactory(new PropertyValueFactory<Row, String>("dst"));
        tbv_dst_file.setCellValueFactory(new PropertyValueFactory<Row, String>("dst_file"));

        mTableView.getItems().setAll(tableData);
    }

    class MyDelPhoto extends delphoto {
        @Override
        public void log(String st) {
            Platform.runLater(() -> mText.appendText(st + "\n"));
        }

        @Override
        public void setProgress(double progr) {
            Platform.runLater(() -> mProgressBar.setProgress(progr));
        }

        @Override
        public void addRow(String src, String src_file, String dst, String dst_file) {
            Platform.runLater(() -> {
                tableData.add(new Row(src, src_file, dst, dst_file));
                //mTableView.getItems().clear();
                mTableView.getItems().setAll(tableData);
                    //    getColumns().get(0).setVisible(false);
                //mTableView.getColumns().get(0).setVisible(true);
            });
        }
    }

    @FXML void onClickRun () {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                MyDelPhoto PH = new MyDelPhoto();
                PH.set_paths(mTextFieldSrc.getText(), mTextFieldDst.getText());
                ArrayList<File> src_folder = PH.get_folders();
                PH.compare_folders(src_folder, false, false);
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML private void onClickCopy() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                MyDelPhoto PH = new MyDelPhoto();
                PH.set_paths(mTextFieldSrc.getText(), mTextFieldDst.getText());
                PH.setCopyTo(mTextCopyDst.getText());
                ArrayList<File> src_folder = PH.get_folders();
                PH.compare_folders(src_folder, true, false);
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML private void onClickCopyDel() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                MyDelPhoto PH = new MyDelPhoto();
                PH.set_paths(mTextFieldSrc.getText(), mTextFieldDst.getText());
                PH.setCopyTo(mTextCopyDst.getText());
                ArrayList<File> src_folder = PH.get_folders();
                PH.compare_folders(src_folder, true, true);
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML private void onClickSelectFrom() {
        DirectoryChooser dir_select = new DirectoryChooser();
        if (Files.notExists(Paths.get(mTextFieldSrc.getText())))
            mTextFieldSrc.setText("C:\\");
        dir_select.setInitialDirectory(new File(mTextFieldSrc.getText()));
        Stage primary_stage = (Stage) mText.getScene().getWindow();
        File from = dir_select.showDialog(primary_stage);
        if (from != null)
            mTextFieldSrc.setText(from.toString());
    }

    @FXML private void onClickSelectTo() {
        DirectoryChooser dir_select = new DirectoryChooser();
        if (Files.notExists(Paths.get(mTextFieldDst.getText())))
            mTextFieldDst.setText("C:\\");
        dir_select.setInitialDirectory(new File(mTextFieldDst.getText()));
        Stage primary_stage = (Stage) mText.getScene().getWindow();
        File from = dir_select.showDialog(primary_stage);
        if (from != null)
            mTextFieldDst.setText(from.toString());
    }
}
