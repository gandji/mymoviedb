package org.gandji.mymoviedb.tools;

import org.gandji.mymoviedb.data.HibernateVideoFileDao;
import org.gandji.mymoviedb.MyMovieDBTestsConfiguration;
import org.gandji.mymoviedb.data.VideoFile;
import org.gandji.mymoviedb.data.repositories.VideoFileRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gandji on 29/11/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyMovieDBTestsConfiguration.class)
public class FillInDiskLabelField {
    private static final Logger LOG = Logger.getLogger(FillInDiskLabelField.class.getName());

    @Autowired
    private HibernateVideoFileDao hibernateVideoFileDao;

    @Test
    public void fillDiskLabel() {
        List<VideoFile> files = hibernateVideoFileDao.findAll();

        Map<String,String> driveLabel = new HashMap<>();

        for (VideoFile vf : files) {

            if (driveLabel.isEmpty()) {
                File file = new File(vf.getDirectory()+"/"+vf.getFileName());
                Iterable<Path> paths = file.toPath().getFileSystem().getRootDirectories();

                for (Path path : paths) {
                    driveLabel.put(path.getRoot().toString(),
                            FileSystemView.getFileSystemView().getSystemDisplayName(path.toFile()));
                    }

                }

            String label = driveLabel.get(vf.getDirectory().substring(0, 3));
            if (null==label) { label = "unknown"; }

            LOG.info("FILE "+vf.getFileName()+" DRIVE "+vf.getDirectory().substring(0,3)+" AND DISK : "+label);
            vf.setDriveLabel(label);
            hibernateVideoFileDao.save(vf);

        }

    }
}
