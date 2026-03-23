package com.example.demo.domain.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "brands", indexes = { @Index(name = "idx_brands_slug", columnList = "slug", unique = true) })
public class Brand {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String name;
    @Column(nullable = false, unique = true, length = 120) private String slug;
    @Column(name = "logo_url") private String logoUrl;
    @Column(nullable = false) private boolean active = true;

    public Brand() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
