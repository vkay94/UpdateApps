package de.vkay.updateapps.AppUebersicht;

public class FeedbackDatatype {

    public String autor, date, message, art;
    public String paketname;

    public FeedbackDatatype(String paketname, String autor, String message, String art, String date){
        this.art = art;
        this.autor = autor;
        this.message = message;
        this.date = date;

        this.paketname = paketname;
    }

    public int hashCode() {
        return String.valueOf(
                this.date.hashCode()
                + this.message.hashCode()
                + this.paketname
                + this.autor
                + this.art
        ).hashCode();
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (!(anotherObject instanceof FeedbackDatatype)) {
            return false;
        }
        FeedbackDatatype p = (FeedbackDatatype) anotherObject;
        return (this.date.equals(p.date) &&
                this.message.equals(p.message) &&
                this.paketname.equals(p.paketname) &&
                this.autor.equals(p.autor) &&
                this.art.equals(p.art)
        );
    }
}
