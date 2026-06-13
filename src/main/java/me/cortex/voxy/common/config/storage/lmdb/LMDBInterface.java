package me.cortex.voxy.common.config.storage.lmdb;

import org.lwjgl.PointerBuffer;

import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.lmdb.LMDB.*;

public class LMDBInterface {
    private final long env;
    private LMDBInterface(long env) {
        this.env = env;
    }

    public void close() {
        mdb_env_close(env);
    }

    public static void E(int rc) {
        if (rc != MDB_SUCCESS) {
            throw new IllegalStateException("Code: " + rc + " msg: " + mdb_strerror(rc));
        }
    }

    public <T> T transaction(TransactionCallback<T> transaction) {
        return transaction(0, transaction);
    }

    public <T> T transaction(int flags, TransactionCallback<T> transaction) {
        return transaction(0, flags, transaction);
    }

    public <T> T transaction(long parent, int flags, TransactionCallback<T> transaction) {
        T ret;
        try (var stack = stackPush()) {
            PointerBuffer pp = stack.mallocPointer(1);
            E(mdb_txn_begin(this.env, parent, flags, pp));
            long txn = pp.get(0);
            int err;
            try {
                ret = transaction.exec(stack, txn);
                err = mdb_txn_commit(txn);
            } catch (Throwable t) {
                mdb_txn_abort(txn);
                throw t;
            }
            E(err);
        }
        return ret;
    }

    public class Database {
        private final int dbi;
        public Database(String name, int flags) {
            this.dbi = LMDBInterface.this.transaction((stack, txn)-> {
                IntBuffer ip = stack.mallocInt(1);
                E(mdb_dbi_open(txn, name, flags, ip));
                return ip.get(0);
            });
        }

        public void close() {
            mdb_dbi_close(LMDBInterface.this.env, this.dbi);
        }

        public int getDBI() {
            return this.dbi;
        }
    }

}
