/*
Daniel Palmer
PROJ-207-A
Workshop 8 - Android App
2022-05-03
 */

package Model;

import java.io.Serializable;
import java.util.Date;

// Class that mirrors the packages table of the TravelExperts database

public class Package implements Serializable {
    private static final long serialVersionUID = 1L;
    private int PackageID;
    private String PkgName;
    private Date PkgStartDate;
    private Date PkgEndDate;
    private String PkgDesc;
    private double PkgBasePrice;
    private double PkgAgencyCommission;

    public Package(int packageID, String pkgName, Date pkgStartDate, Date pkgEndDate, String pkgDesc, double pkgBasePrice, double pkgAgencyCommission) {
        PackageID = packageID;
        PkgName = pkgName;
        PkgStartDate = pkgStartDate;
        PkgEndDate = pkgEndDate;
        PkgDesc = pkgDesc;
        PkgBasePrice = pkgBasePrice;
        PkgAgencyCommission = pkgAgencyCommission;
    }
    // Getters and Setters
    public int getPackageID() {
        return PackageID;
    }

    public void setPackageID(int packageID) {
        PackageID = packageID;
    }

    public String getPkgName() {
        return PkgName;
    }

    public void setPkgName(String pkgName) {
        PkgName = pkgName;
    }

    public Date getPkgStartDate() {
        return PkgStartDate;
    }

    public void setPkgStartDate(Date pkgStartDate) {
        PkgStartDate = pkgStartDate;
    }

    public Date getPkgEndDate() {
        return PkgEndDate;
    }

    public void setPkgEndDate(Date pkgEndDate) {
        PkgEndDate = pkgEndDate;
    }

    public String getPkgDesc() {
        return PkgDesc;
    }

    public void setPkgDesc(String pkgDesc) {
        PkgDesc = pkgDesc;
    }

    public double getPkgBasePrice() {
        return PkgBasePrice;
    }

    public void setPkgBasePrice(double pkgBasePrice) {
        PkgBasePrice = pkgBasePrice;
    }

    public double getPkgAgencyCommission() {
        return PkgAgencyCommission;
    }

    public void setPkgAgencyCommission(double pkgAgencyCommission) {
        PkgAgencyCommission = pkgAgencyCommission;
    }

    @Override
    public String toString() {
        return PkgName;
    }
}
