import java.math.BigInteger;
import java.util.Random;

public final class CriptoShamir {

    public final class secretoCompartida {
        public secretoCompartida(final int num, final BigInteger Compartida) {
            this.num = num;
            this.Compartida = Compartida;
        }

        public int getNum() {
            return num;
        }

        public BigInteger getCompartida() {
            return Compartida;
        }

        @Override
        public String toString() {
            return "secreto Compartido [num=" + num + ", Compartida=" + Compartida + "]";
        }

        private final int num;
        private final BigInteger Compartida;
    }

    public CriptoShamir(final int k, final int n) {
        this.k = k;
        this.n = n;

        random = new Random();
    }

    public secretoCompartida[] split(final BigInteger secreto) {
        final int modLength = secreto.bitLength() + 1;

        prime = new BigInteger(modLength, CERTAINTY, random);
        final BigInteger[] coeff = new BigInteger[k - 1];

        System.out.println("Prime Number: " + prime);

        for (int i = 0; i < k - 1; i++) {
            coeff[i] = randomZp(prime);
            System.out.println("P" + (i) + "): " + coeff[i]);
        }

        final secretoCompartida[] Compartidas = new secretoCompartida[n];
        for (int i = 1; i <= n; i++) {
            BigInteger accum = secreto;

            for (int j = 1; j < k; j++) {
                final BigInteger t1 = BigInteger.valueOf(i).modPow(BigInteger.valueOf(j), prime);
                final BigInteger t2 = coeff[j - 1].multiply(t1).mod(prime);

                accum = accum.add(t2).mod(prime);
            }
            Compartidas[i - 1] = new secretoCompartida(i - 1, accum);
            System.out.println("Compartida " + Compartidas[i - 1]);
        }

        return Compartidas;
    }

    public BigInteger getPrime() {
        return prime;
    }

    public BigInteger combine(final secretoCompartida[] Compartidas, final BigInteger primeNum) {
        BigInteger accum = BigInteger.ZERO;
        for (int i = 0; i < k; i++) {
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                	num = num.multiply(BigInteger.valueOf(-j - 1)).mod(primeNum);
                    den = den.multiply(BigInteger.valueOf(i - j)).mod(primeNum);
                }
            }

            System.out.println("den: " + den + ", num: " + den + ", inv: " + den.modInverse(primeNum));
            final BigInteger Valor = Compartidas[i].getCompartida();

            final BigInteger tmp = Valor.multiply(num).multiply(den.modInverse(primeNum)).mod(primeNum);
            accum = accum.add(primeNum).add(tmp).mod(primeNum);

            System.out.println("Valor: " + Valor + ", tmp: " + tmp + ", accum: " + accum);
        }

        System.out.println("El secreto es: " + accum);

        return accum;
    }

    private BigInteger randomZp(final BigInteger p) {
        while (true) {
            final BigInteger r = new BigInteger(p.bitLength(), random);
            if (r.compareTo(BigInteger.ZERO) > 0 && r.compareTo(p) < 0) {
                return r;
            }
        }
    }

    private BigInteger prime;

    private final int k;
    private final int n;
    private final Random random;

    private static final int CERTAINTY = 50;

    public static void main(final String[] args) {
        final CriptoShamir CriptoShamir = new CriptoShamir(11, 20);

        final BigInteger secreto = new BigInteger("1234567890123456789012345678901234567890");
        final secretoCompartida[] Compartidas = CriptoShamir.split(secreto);
        final BigInteger prime = CriptoShamir.getPrime();

        final CriptoShamir CriptoShamir2 = new CriptoShamir(11, 20);
        final BigInteger respuesta = CriptoShamir2.combine(Compartidas, prime);

    }
}