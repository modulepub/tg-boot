package pub.module.architecture;

import org.junit.jupiter.api.Test;
import pub.module.common.messaging.TransactionAfterCommit;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionAfterCommitTest {

    @Test
    void runsImmediatelyWhenNoActiveTransaction() {
        AtomicBoolean ran = new AtomicBoolean(false);
        TransactionAfterCommit.runAfterCommit(() -> ran.set(true));
        assertTrue(ran.get());
    }
}
