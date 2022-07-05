import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public final class Counter {
    private final String prefix;
    private final String suffix;
    private final AtomicLong counter = new AtomicLong(0);
    private final AtomicLong nextCounter = new AtomicLong(1);
    private static final Logger log = LoggerFactory.getLogger(Counter.class);

    /**
     * @param prefix Some text that is output just before the counter-value.
     */
    public Counter(final String prefix) {
        this( prefix , "" );
    }

    /**
     * @param prefix Some text that is output just before the counter-value.
     * @param suffix Some text that is output just after the counter-value.
     */
    public Counter(final String prefix, final String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public void incCounter() {
        long i = this.counter.incrementAndGet();
        long n = this.nextCounter.get();
        if (i >= n) {
            if (this.nextCounter.compareAndSet(n, n*2)) {
                log.info(this.prefix + n + this.suffix);
            }
        }
    }

    public void printCounter() {
        log.info(this.prefix + this.counter.get() + this.suffix);
    }

    public long getCounter() {
        return this.counter.get();
    }

    public void reset() {
        this.counter.set(0);
        this.nextCounter.set(1);
    }
}