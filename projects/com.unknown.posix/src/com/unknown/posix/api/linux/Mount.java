package com.unknown.posix.api.linux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.unknown.util.BitTest;

public class Mount {
	// @formatter:off
	public static final int MS_RDONLY = 1;                /* Mount read-only.  */
	public static final int MS_NOSUID = 2;                /* Ignore suid and sgid bits.  */
	public static final int MS_NODEV = 4;                 /* Disallow access to device special files.  */
	public static final int MS_NOEXEC = 8;                /* Disallow program execution.  */
	public static final int MS_SYNCHRONOUS = 16;          /* Writes are synced at once.  */
	public static final int MS_REMOUNT = 32;              /* Alter flags of a mounted FS.  */
	public static final int MS_MANDLOCK = 64;             /* Allow mandatory locks on an FS.  */
	public static final int MS_DIRSYNC = 128;             /* Directory modifications are synchronous.  */
	public static final int MS_NOSYMFOLLOW = 256;         /* Do not follow symlinks.  */
	public static final int MS_NOATIME = 1024;            /* Do not update access times.  */
	public static final int MS_NODIRATIME = 2048;         /* Do not update directory access times.  */
	public static final int MS_BIND = 4096;               /* Bind directory at different place.  */
	public static final int MS_MOVE = 8192;
	public static final int MS_REC = 16384;
	public static final int MS_SILENT = 32768;
	public static final int MS_POSIXACL = 1 << 16;        /* VFS does not apply the umask.  */
	public static final int MS_UNBINDABLE = 1 << 17;      /* Change to unbindable.  */
	public static final int MS_PRIVATE = 1 << 18;         /* Change to private.  */
	public static final int MS_SLAVE = 1 << 19;           /* Change to slave.  */
	public static final int MS_SHARED = 1 << 20;          /* Change to shared.  */
	public static final int MS_RELATIME = 1 << 21;        /* Update atime relative to mtime/ctime.  */
	public static final int MS_KERNMOUNT = 1 << 22;       /* This is a kern_mount call.  */
	public static final int MS_I_VERSION =  1 << 23;      /* Update inode I_version field.  */
	public static final int MS_STRICTATIME = 1 << 24;     /* Always perform atime updates.  */
	public static final int MS_LAZYTIME = 1 << 25;        /* Update the on-disk [acm]times lazily.  */
	public static final int MS_ACTIVE = 1 << 30;
	public static final int MS_NOUSER = 1 << 31;

	/* Flags that can be altered by MS_REMOUNT */
	public static final int MS_RMT_MASK = (MS_RDONLY|MS_SYNCHRONOUS|MS_MANDLOCK|MS_I_VERSION |MS_LAZYTIME);

	/* Magic mount flag number. Has to be or-ed to the flag values. */
	public static final int MS_MGC_VAL = 0xc0ed0000;      /* Magic flag number to indicate "new" flags */
	public static final int MS_MGC_MSK = 0xffff0000;      /* Magic flag number mask */

	/* Possible value for FLAGS parameter of `umount2'. */
	public static final int MNT_FORCE = 1;                /* Force unmounting.  */
	public static final int MNT_DETACH = 2;               /* Just detach from the tree.  */
	public static final int MNT_EXPIRE = 4;               /* Mark for expiry.  */
	public static final int UMOUNT_NOFOLLOW = 8;          /* Don't follow symlink on umount.  */

	/* fsmount flags.  */
	public static final int FSMOUNT_CLOEXEC        = 0x00000001;

	/* mount attributes used on fsmount.  */
	public static final int MOUNT_ATTR_RDONLY      = 0x00000001; /* Mount read-only.  */
	public static final int MOUNT_ATTR_NOSUID      = 0x00000002; /* Ignore suid and sgid bits.  */
	public static final int MOUNT_ATTR_NODEV       = 0x00000004; /* Disallow access to device special files.  */
	public static final int MOUNT_ATTR_NOEXEC      = 0x00000008; /* Disallow program execution.  */
	public static final int MOUNT_ATTR__ATIME      = 0x00000070; /* Setting on how atime should be updated.  */
	public static final int MOUNT_ATTR_RELATIME    = 0x00000000; /* - Update atime relative to mtime/ctime.  */
	public static final int MOUNT_ATTR_NOATIME     = 0x00000010; /* - Do not update access times.  */
	public static final int MOUNT_ATTR_STRICTATIME = 0x00000020; /* - Always perform atime updates  */
	public static final int MOUNT_ATTR_NODIRATIME  = 0x00000080; /* Do not update directory access times.  */
	public static final int MOUNT_ATTR_IDMAP       = 0x00100000; /* Idmap mount to @userns_fd in struct mount_attr.  */
	public static final int MOUNT_ATTR_NOSYMFOLLOW = 0x00200000; /* Do not follow symlinks.  */
	// @formatter:off

	public static final String mountflags(long mountflags) {
		long flags = mountflags & ~MS_MGC_VAL;
		List<String> result = new ArrayList<>();

		if(BitTest.test(flags, MS_RDONLY)) {
			result.add("MS_RDONLY");
		}
		if(BitTest.test(flags, MS_NOSUID)) {
			result.add("MS_NOSUID");
		}
		if(BitTest.test(flags, MS_NODEV)) {
			result.add("MS_NODEV");
		}
		if(BitTest.test(flags, MS_NOEXEC)) {
			result.add("MS_NOEXEC");
		}
		if(BitTest.test(flags, MS_SYNCHRONOUS)) {
			result.add("MS_SYNCHRONOUS");
		}
		if(BitTest.test(flags, MS_REMOUNT)) {
			result.add("MS_REMOUNT");
		}
		if(BitTest.test(flags, MS_MANDLOCK)) {
			result.add("MS_MANDLOCK");
		}
		if(BitTest.test(flags, MS_DIRSYNC)) {
			result.add("MS_DIRSYNC");
		}
		if(BitTest.test(flags, MS_NOSYMFOLLOW)) {
			result.add("MS_NOSYMFOLLOW");
		}
		if(BitTest.test(flags, MS_NOATIME)) {
			result.add("MS_NOATIME");
		}
		if(BitTest.test(flags, MS_NODIRATIME)) {
			result.add("MS_NODIRATIME");
		}
		if(BitTest.test(flags, MS_BIND)) {
			result.add("MS_BIND");
		}
		if(BitTest.test(flags, MS_MOVE)) {
			result.add("MS_MOVE");
		}
		if(BitTest.test(flags, MS_REC)) {
			result.add("MS_REC");
		}
		if(BitTest.test(flags, MS_SILENT)) {
			result.add("MS_SILENT");
		}
		if(BitTest.test(flags, MS_POSIXACL)) {
			result.add("MS_POSIXACL");
		}
		if(BitTest.test(flags, MS_UNBINDABLE)) {
			result.add("MS_UNBINDABLE");
		}
		if(BitTest.test(flags, MS_PRIVATE)) {
			result.add("MS_PRIVATE");
		}
		if(BitTest.test(flags, MS_SLAVE)) {
			result.add("MS_SLAVE");
		}
		if(BitTest.test(flags, MS_SHARED)) {
			result.add("MS_SHARED");
		}
		if(BitTest.test(flags, MS_RELATIME)) {
			result.add("MS_RELATIME");
		}
		if(BitTest.test(flags, MS_KERNMOUNT)) {
			result.add("MS_KERNMOUNT");
		}
		if(BitTest.test(flags, MS_I_VERSION)) {
			result.add("MS_I_VERSION");
		}
		if(BitTest.test(flags, MS_STRICTATIME)) {
			result.add("MS_STRICTATIME");
		}
		if(BitTest.test(flags, MS_LAZYTIME)) {
			result.add("MS_LAZYTIME");
		}
		if(BitTest.test(flags, MS_ACTIVE)) {
			result.add("MS_ACTIVE");
		}
		if(BitTest.test(flags, MS_NOUSER)) {
			result.add("MS_NOUSER");
		}

		if(result.size() == 0) {
			return "0";
		} else {
			return result.stream().collect(Collectors.joining("|"));
		}
	}

	public static final String umountflags(long flags) {
		List<String> result = new ArrayList<>();

		if(BitTest.test(flags, MNT_FORCE)) {
			result.add("MNT_FORCE");
		}
		if(BitTest.test(flags, MNT_DETACH)) {
			result.add("MNT_DETACH");
		}
		if(BitTest.test(flags, MNT_EXPIRE)) {
			result.add("MNT_EXPIRE");
		}
		if(BitTest.test(flags, UMOUNT_NOFOLLOW)) {
			result.add("UMOUNT_NOFOLLOW");
		}

		if(result.size() == 0) {
			return "0";
		} else {
			return result.stream().collect(Collectors.joining("|"));
		}
	}
}
