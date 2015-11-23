package domainregistry;

public abstract class DomainRegistryException extends RuntimeException implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public DomainRegistryException(String s) {super(s);}
    public DomainRegistryException() {}

}
