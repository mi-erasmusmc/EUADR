package nl.erasmusmc.bios.euadr.signals;

public class DrugDisease {
    private String drugName = null;
    private String drugId = null;
    private String disease = null;
    
    public DrugDisease(String drugName, String drugId, String disease) {
	setDrugName(drugName);
	setDrugId(drugId);
	setDisease(disease);
    }

    public String getDrugName() {
        return drugName;
    }
    
    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }
    
    public String getDisease() {
        return disease;
    }
    
    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDrugId() {
	return drugId;
    }

    public void setDrugId(String drugId) {
	this.drugId = drugId;
    }
}
