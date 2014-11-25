package codingpark.net.cheesecloud.handle;

import java.util.ArrayList;

import codingpark.net.cheesecloud.entity.DownloadFile;
import codingpark.net.cheesecloud.entity.UploadFile;

/**
 * Created by ethanshan on 14-11-19.
 */
public interface OnTransFragmentInteractionListener {
    public void onFragmentInteraction(String id);
    public void refreshUploadBottomBar(ArrayList<UploadFile> waitUploadFile, ArrayList<UploadFile> uploadingFile,
                                       ArrayList<UploadFile> pauseUploadFile, ArrayList<UploadFile> uploadedFile);
    public void refreshDownloadBottomBar(ArrayList<DownloadFile> waitDownloadFile, ArrayList<DownloadFile> uploadingFile,
                                       ArrayList<DownloadFile> pauseDownloadFile, ArrayList<DownloadFile> uploadedFile);
}
