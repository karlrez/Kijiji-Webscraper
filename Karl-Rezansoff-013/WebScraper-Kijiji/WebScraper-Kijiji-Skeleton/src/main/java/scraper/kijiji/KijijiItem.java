/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper.kijiji;

/**
 *
 * @author karlr
 */
public class KijijiItem {
    
    // UML shows <<Property>> for these methods
    String id;
    String url;
    String imageUrl;
    String imageName;
    String price;
    String title;
    String date;
    String location;
    String description;
    
    public KijijiItem() {}
    
    public String getUrl() { return url; }
    void setUrl(String url) { this.url = url; }
    
    public String getId() { return id; }
    void setId (String id) { this.id = id; }
    
    public String getImageUrl() { return imageUrl; }
    void setImageUrl( String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getImageName() { return imageName; }
    void setImageName(String imageName) { this.imageName = imageName; } 
    
    public String getPrice() { return price; }
    void setPrice(String price) { this.price = price; }
    
    public String getTitle() { return title; }
    void setTitle(String title) { this.title = title; }
    
    public String getDate() { return date; }
    void setDate(String date) { this.date = date; }
    
    public String getLocation() { return location; }
    void setLocation(String location) { this.location = location; }
    
    public String getDescription() { return description; }
    void setDescription(String description) { this.description = description; }
    
    // Copied from the hashCode methods in the entity classes
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KijijiItem)) {
            return false;
        }
        KijijiItem other = (KijijiItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("[id:%s, image_url:%s, image_name:%s, price:%s, title:%s, date:%s, location:%s, description:%s]",
                getId(), getImageUrl(), getImageName(), getPrice(), getTitle(), getDate(), getLocation(), getDescription());
    }
}
