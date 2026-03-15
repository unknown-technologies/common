/* vim:set ts=8 sts=8 sw=8 tw=80 cc=80 noet: */
#include "com_unknown_platform_serial_RS232.h"

static const char* ioe_name = "java/io/IOException";
static const char* iae_name = "java/lang/IllegalArgumentException";
static const char* ioobe_name = "java/lang/IndexOutOfBoundsException";
static const char* oome_name = "java/lang/OutOfMemoryError";
static const char* npe_name = "java/lang/NullPointerException";

#ifdef __linux__
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <termios.h>
#include <fcntl.h>
#include <poll.h>

static const char* eofe_name = "java/io/EOFException";

typedef struct {
	int	fd;
	int	pipe[2];
	struct termios tio;
} FD;

int int2baud(int baud) {
	switch(baud) {
		case 0:		return B0;
		case 50:	return B50;
		case 75:	return B75;
		case 110:	return B110;
		case 134:	return B134;
		case 150:	return B150;
		case 200:	return B200;
		case 300:	return B300;
		case 600:	return B600;
		case 1200:	return B1200;
		case 1800:	return B1800;
		case 2400:	return B2400;
		case 4800:	return B4800;
		case 9600:	return B9600;
		case 19200:	return B19200;
		case 38400:	return B38400;
		case 57600:	return B57600;
		case 115200:	return B115200;
		case 230400:	return B230400;
		case 460800:	return B460800;
		case 500000:	return B500000;
		case 576000:	return B576000;
		case 921600:	return B921600;
		case 1000000:	return B1000000;
		case 1152000:	return B1152000;
		case 1500000:	return B1500000;
		case 2000000:	return B2000000;
		case 2500000:	return B2500000;
		case 3000000:	return B3000000;
		case 3500000:	return B3500000;
		case 4000000:	return B4000000;
		default:	return -1;
	}
}

JNIEXPORT jlong JNICALL Java_com_unknown_platform_serial_RS232_open
  (JNIEnv* env, jclass self, jstring filename)
{
	const char* path = (*env)->GetStringUTFChars(env, filename, NULL);
	if(!path) {
		jclass clazz = (*env)->FindClass(env, npe_name);
		(*env)->ThrowNew(env, clazz, "Invalid filename");
		return 0;
	}

	int fd = open(path, O_RDWR | O_NOCTTY | O_NONBLOCK);
	if(fd == -1) {
		const char* msg = strerror(errno);

		(*env)->ReleaseStringUTFChars(env, filename, path);

		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz, msg);
		return 0;
	}

	(*env)->ReleaseStringUTFChars(env, filename, path);

	struct termios tio;
	if(tcgetattr(fd, &tio) == -1) {
		const char* msg = strerror(errno);
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz, msg);
		close(fd);
		return 0;
	}

	FD* jfd = (FD*) malloc(sizeof(FD));
	if(!jfd) {
		jclass clazz = (*env)->FindClass(env, oome_name);
		(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
		close(fd);
		return 0;
	}

	jfd->fd = fd;
	jfd->tio = tio;

	if(pipe(jfd->pipe)) {
		const char* msg = strerror(errno);
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz, msg);
		close(fd);
		free(jfd);
		return 0;
	}

	return (jlong) jfd;
}

JNIEXPORT void JNICALL Java_com_unknown_platform_serial_RS232_close
  (JNIEnv* env, jclass self, jlong fd)
{
	FD* jfd = (FD*) fd;

	// signal all read threads
	if(write(jfd->pipe[1], "Q", 1) != 1) {
		const char* msg = strerror(errno);
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz, msg);
	}

	// close pipe
	close(jfd->pipe[1]);
	close(jfd->pipe[0]);

	// reset TTY config
	if(tcsetattr(jfd->fd, TCSANOW, &jfd->tio) == -1) {
		const char* msg = strerror(errno);
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz, msg);
	}

	// close TTY file descriptor
	close(jfd->fd);

	// finally free native config object
	free(jfd);
}

JNIEXPORT jboolean JNICALL Java_com_unknown_platform_serial_RS232_configure
  (JNIEnv* env, jclass self, jlong fd, jint baud, jint format)
{
	FD* jfd = (FD*) fd;

	struct termios tio;
	int rate = int2baud(baud);

	if(rate == -1) {
		jclass clazz = (*env)->FindClass(env, iae_name);
		(*env)->ThrowNew(env, clazz, "Illegal baud rate");
		return 0;
	}


	if(tcgetattr(jfd->fd, &tio) == -1) {
		const char* msg = strerror(errno);
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz, msg);
		return 0;
	}

	cfsetispeed(&tio, rate);
	cfsetospeed(&tio, rate);

	/* set raw mode */
	tio.c_iflag &= ~(BRKINT | ICRNL | INPCK | ISTRIP);
	tio.c_iflag &= ~(IXON | IXOFF);
	tio.c_iflag |= IGNBRK | IGNPAR;
	tio.c_oflag &= ~OPOST;
	tio.c_lflag &= ~(ECHO | ICANON | IEXTEN | ISIG);

	tio.c_cc[VMIN] = 0;
	tio.c_cc[VTIME] = 0; /* immediate - anything */

	switch(format) {
		case 0: /* 8N1, no flow control */
			tio.c_cflag &= ~CSIZE;
			tio.c_cflag |= CS8;
			tio.c_cflag &= ~(PARENB | CSTOPB);
			tio.c_cflag &= ~CRTSCTS;
			break;
		case 1: /* 8N1, hardware flow control */
			tio.c_cflag &= ~CSIZE;
			tio.c_cflag |= CS8;
			tio.c_cflag &= ~(PARENB | CSTOPB);
			tio.c_cflag |= CRTSCTS;
			break;
	}

	if(tcsetattr(jfd->fd, TCSADRAIN, &tio) == -1) {
		const char* msg = strerror(errno);
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz, msg);
		return 0;
	}

	return 1;
}

JNIEXPORT jint JNICALL Java_com_unknown_platform_serial_RS232_read
  (JNIEnv* env, jclass self, jlong fd, jbyteArray b, jint off, jint len)
{
	FD* jfd = (FD*) fd;

	if(!b) {
		jclass clazz = (*env)->FindClass(env, npe_name);
		(*env)->ThrowNew(env, clazz, "not a valid data buffer");
		return 0;
	}

	if(!len) // read 0 bytes
		return 0;

	const jsize blen = (*env)->GetArrayLength(env, b);
	if(((off + len) > blen) || (off < 0) || (len < 0)) { // check bounds
		jclass clazz = (*env)->FindClass(env, ioobe_name);
		(*env)->ThrowNew(env, clazz, "index out of bounds");
		return 0;
	}

	// wait for events
	struct pollfd fds[2] = {
		{ .fd = jfd->fd, .events = POLLIN },
		{ .fd = jfd->pipe[0], .events = POLLIN }
	};

	int result = poll(fds, 2, -1);
	if(result > 0) {
		if(fds[1].revents & (POLLIN | POLLHUP | POLLERR | POLLNVAL)) {
			jclass clazz = (*env)->FindClass(env, eofe_name);
			(*env)->ThrowNew(env, clazz, "Connection closed");
			return 0;
		}

		if(fds[0].revents & (POLLHUP | POLLERR | POLLNVAL)) {
			jclass clazz = (*env)->FindClass(env, eofe_name);
			(*env)->ThrowNew(env, clazz, "I/O error");
			return 0;
		}
	} else if(result == 0) {
		return 0;
	} else {
		const char* msg = strerror(errno);
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz, msg);
		return 0;
	}

	jbyte* buf = (*env)->GetByteArrayElements(env, b, NULL);
	if(!buf) {
		jclass clazz = (*env)->FindClass(env, oome_name);
		(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
		return 0;
	}

	ssize_t bytes_read = read(jfd->fd, buf + off, len);
	if(bytes_read == -1) {
		const char* msg = strerror(errno);
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz, msg);
		(*env)->ReleaseByteArrayElements(env, b, buf, JNI_COMMIT);
		return 0;
	}

	(*env)->ReleaseByteArrayElements(env, b, buf, JNI_COMMIT);

	return bytes_read;
}

JNIEXPORT jint JNICALL Java_com_unknown_platform_serial_RS232_write
  (JNIEnv* env, jclass self, jlong fd, jbyteArray b, jint off, jint len)
{
	FD* jfd = (FD*) fd;

	if(!b) {
		jclass clazz = (*env)->FindClass(env, npe_name);
		(*env)->ThrowNew(env, clazz, "not a valid data buffer");
		return 0;
	}

	if(!len) // write 0 bytes
		return 0;

	const jsize blen = (*env)->GetArrayLength(env, b);
	if(((off + len) > blen) || (off < 0) || (len < 0)) { // check bounds
		jclass clazz = (*env)->FindClass(env, ioobe_name);
		(*env)->ThrowNew(env, clazz, "index out of bounds");
		return 0;
	}

	jbyte* buf = (jbyte*) calloc(len, sizeof(jbyte));
	if(!buf) {
		jclass clazz = (*env)->FindClass(env, oome_name);
		(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
		return 0;
	}

	(*env)->GetByteArrayRegion(env, b, off, len, buf);

	ssize_t bytes_written = 0;
	ssize_t total = 0;
	jbyte* ptr = buf;
	while(len > 0) {
		bytes_written = write(jfd->fd, ptr, len);
		if(bytes_written == -1) {
			if(errno != EAGAIN) {
				const char* msg = strerror(errno);
				jclass clazz = (*env)->FindClass(env, ioe_name);
				(*env)->ThrowNew(env, clazz, msg);
			}
			free(buf);
			return total;
		}
		len -= bytes_written;
		ptr += bytes_written;
		total += bytes_written;
	}

	free(buf);
	return total;
}

#elif _WIN32
#include <windows.h>

JNIEXPORT jlong JNICALL Java_com_unknown_platform_serial_RS232_open
  (JNIEnv* env, jclass self, jstring filename)
{
	const char* path = (*env)->GetStringUTFChars(env, filename, NULL);
	if(!path) {
		jclass clazz = (*env)->FindClass(env, npe_name);
		(*env)->ThrowNew(env, clazz, "Invalid filename");
		return 0;
	}

	HANDLE com = CreateFile(path, GENERIC_READ | GENERIC_WRITE, 0, NULL,
			OPEN_EXISTING, FILE_FLAG_OVERLAPPED, NULL);

	(*env)->ReleaseStringUTFChars(env, filename, path);

	if(com == INVALID_HANDLE_VALUE) {
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz, "Failed to open file");
		return 0;
	}

	if(!SetCommMask(com, EV_RXCHAR)) {
		CloseHandle(com);
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz, "Failed to set comm mask");
		return 0;
	}

	return (jlong) com;
}

JNIEXPORT void JNICALL Java_com_unknown_platform_serial_RS232_close
  (JNIEnv* env, jclass self, jlong fd)
{
	HANDLE com = (HANDLE) fd;

	CancelIoEx(com, NULL);

	CloseHandle(com);
}

JNIEXPORT jboolean JNICALL Java_com_unknown_platform_serial_RS232_configure
  (JNIEnv* env, jclass self, jlong fd, jint baud, jint format)
{
	HANDLE com = (HANDLE) fd;

	DCB params = { 0 };
	params.DCBlength = sizeof(params);

	if(!GetCommState(com, &params)) {
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz,
				"Failed to retrieve serial line configuration");
		return 0;
	}

	params.BaudRate = baud;

	/* TODO: add more formats */
	switch(format) {
		default:
		case 0: /* 8N1, no flow control */
			params.ByteSize     = 8;
			params.StopBits     = ONESTOPBIT;
			params.Parity       = NOPARITY;
			params.fBinary      = TRUE;
			params.fParity      = FALSE;
			params.fOutxCtsFlow = FALSE;
			params.fOutxDsrFlow = FALSE;
			params.fOutX        = FALSE;
			params.fInX         = FALSE;
			params.fErrorChar   = FALSE;
			params.fNull        = FALSE;
			params.fDsrSensitivity = FALSE;
			params.fDtrControl  = DTR_CONTROL_ENABLE;
			params.fRtsControl  = RTS_CONTROL_ENABLE;
			break;
		case 1: /* 8N1, hardware flow control */
			params.ByteSize     = 8;
			params.StopBits     = ONESTOPBIT;
			params.Parity       = NOPARITY;
			params.fBinary      = TRUE;
			params.fParity      = FALSE;
			params.fOutxCtsFlow = TRUE;
			params.fOutxDsrFlow = FALSE;
			params.fOutX        = FALSE;
			params.fInX         = FALSE;
			params.fErrorChar   = FALSE;
			params.fNull        = FALSE;
			params.fDsrSensitivity = FALSE;
			params.fDtrControl  = DTR_CONTROL_ENABLE;
			params.fRtsControl  = RTS_CONTROL_HANDSHAKE;
			break;
	}

	if(!SetCommState(com, &params)) {
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz,
				"Failed to set serial line configuration");
		return 0;
	}

	COMMTIMEOUTS timeouts = { 0 };
	timeouts.ReadIntervalTimeout = 1;
	timeouts.ReadTotalTimeoutConstant = 0;
	timeouts.ReadTotalTimeoutMultiplier = 0;
	timeouts.WriteTotalTimeoutConstant = 0;
	timeouts.WriteTotalTimeoutMultiplier = 0;

	if(!SetCommTimeouts(com, &timeouts)) {
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz,
				"Failed to set serial line timeouts");
		return 0;
	}

	if(!SetCommMask(com, EV_RXCHAR)) {
		jclass clazz = (*env)->FindClass(env, ioe_name);
		(*env)->ThrowNew(env, clazz,
				"Failed to set comm mask");
		return 0;
	}

	return 1;
}

JNIEXPORT jint JNICALL Java_com_unknown_platform_serial_RS232_read
  (JNIEnv* env, jclass self, jlong fd, jbyteArray b, jint off, jint len)
{
	HANDLE com = (HANDLE) fd;

	if(!b) {
		jclass clazz = (*env)->FindClass(env, npe_name);
		(*env)->ThrowNew(env, clazz, "not a valid data buffer");
		return 0;
	}

	if(!len) // write 0 bytes
		return 0;

	const jsize blen = (*env)->GetArrayLength(env, b);
	if(((off + len) > blen) || (off < 0) || (len < 0)) { // check bounds
		jclass clazz = (*env)->FindClass(env, ioobe_name);
		(*env)->ThrowNew(env, clazz, "index out of bounds");
		return 0;
	}

	jbyte* buf = (*env)->GetByteArrayElements(env, b, NULL);
	if(!buf) {
		jclass clazz = (*env)->FindClass(env, oome_name);
		(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
		return 0;
	}

	DWORD bytes_read = 0;
	OVERLAPPED read_state = { 0 };
	read_state.hEvent = CreateEvent(0, TRUE, FALSE, 0);

	if(!ReadFile(com, buf + off, len, &bytes_read, &read_state)) {
		if(GetLastError() != ERROR_IO_PENDING) {
			CloseHandle(read_state.hEvent);
			(*env)->ReleaseByteArrayElements(env, b, buf, JNI_ABORT);
			jclass clazz = (*env)->FindClass(env, ioe_name);
			(*env)->ThrowNew(env, clazz, "error while reading");
			return 0;
		}

		switch(WaitForSingleObject(read_state.hEvent, INFINITE)) {
			case WAIT_OBJECT_0:
			case WAIT_TIMEOUT:
				if(!GetOverlappedResult(com, &read_state, &bytes_read, FALSE)) {
					CloseHandle(read_state.hEvent);
					(*env)->ReleaseByteArrayElements(env, b, buf, JNI_ABORT);
					jclass clazz = (*env)->FindClass(env, ioe_name);
					(*env)->ThrowNew(env, clazz, "GetOverlappedResult failed");
					return 0;
				}
				break;
			default:
			case WAIT_FAILED: {
				CloseHandle(read_state.hEvent);
				(*env)->ReleaseByteArrayElements(env, b, buf, JNI_ABORT);
				jclass clazz = (*env)->FindClass(env, ioe_name);
				(*env)->ThrowNew(env, clazz, "WaitForSingleObject failed");
				return 0;
			}
			case WAIT_ABANDONED: {
				CloseHandle(read_state.hEvent);
				(*env)->ReleaseByteArrayElements(env, b, buf, JNI_ABORT);
				jclass clazz = (*env)->FindClass(env, ioe_name);
				(*env)->ThrowNew(env, clazz, "WaitForSingleObject abandoned");
				return 0;
			}
		}

	}

	(*env)->ReleaseByteArrayElements(env, b, buf, JNI_COMMIT);

	CloseHandle(read_state.hEvent);

	return bytes_read;
}

JNIEXPORT jint JNICALL Java_com_unknown_platform_serial_RS232_write
  (JNIEnv* env, jclass self, jlong fd, jbyteArray b, jint off, jint len)
{
	HANDLE com = (HANDLE) fd;

	if(!b) {
		jclass clazz = (*env)->FindClass(env, npe_name);
		(*env)->ThrowNew(env, clazz, "not a valid data buffer");
		return 0;
	}

	if(!len) // write 0 bytes
		return 0;

	const jsize blen = (*env)->GetArrayLength(env, b);
	if(((off + len) > blen) || (off < 0) || (len < 0)) { // check bounds
		jclass clazz = (*env)->FindClass(env, ioobe_name);
		(*env)->ThrowNew(env, clazz, "index out of bounds");
		return 0;
	}

	jbyte* buf = (jbyte*) calloc(len, sizeof(jbyte));
	if(!buf) {
		jclass clazz = (*env)->FindClass(env, oome_name);
		(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
		return 0;
	}

	(*env)->GetByteArrayRegion(env, b, off, len, buf);

	DWORD commEvent;
	OVERLAPPED write_state = { 0 };
	DWORD bytes_written = 0;

	write_state.hEvent = CreateEvent(NULL, TRUE, FALSE, NULL);

	if(!WriteFile(com, buf, len, &bytes_written, &write_state)) {
		if(GetLastError() != ERROR_IO_PENDING) {
			jclass clazz = (*env)->FindClass(env, ioe_name);
			(*env)->ThrowNew(env, clazz, "error while writing");
			CloseHandle(write_state.hEvent);
			free(buf);
			return 0;
		} else {
			if(!GetOverlappedResult(com, &write_state, &bytes_written, TRUE)) {
				jclass clazz = (*env)->FindClass(env, ioe_name);
				(*env)->ThrowNew(env, clazz, "error while writing");
				CloseHandle(write_state.hEvent);
				free(buf);
				return 0;
			}
		}
	}

	free(buf);

	CloseHandle(write_state.hEvent);

	return bytes_written;
}

#endif
