package aecb.aecbeacons2;


public class AecbImage {

    // class variables
    private int id;
    private String beacon;
    private String image_url;
    private String thumbnail_image_url;

    // constructors
    public AecbImage() {
        id = -1;
        beacon = "";
        image_url = "";
        thumbnail_image_url = "";
    }

    public AecbImage(int id, String beacon, String image_url, String thumbnail_image_url) {
        this.id = id;
        this.beacon = beacon;
        this.image_url = image_url;
        this.thumbnail_image_url = thumbnail_image_url;
    }

    public int getId() {
        return id;
    }

    public String getBeacon() {
        return beacon;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getThumbnail_image_url() {
        return thumbnail_image_url;
    }


}