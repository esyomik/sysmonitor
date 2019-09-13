#include <stdarg.h>
#include <io.h>
#include <stdlib.h>

#include "file.hpp"

#pragma warning(disable : 4996)


File::File() : _pF(nullptr) {
    ;
}


File::File(const char *path, const char *mode) {
    _pF = nullptr;
    open(path, mode);
}


File::File(const wchar_t *path, const wchar_t *mode) {
    _pF = nullptr;
    open(path, mode);
}


File::File(StdStream idStream) {
    _pF = nullptr;
    open(idStream);
}


File::~File() {
    if (_pF) {
        fclose(_pF);
    }
}


bool File::open(const char *path, const char *mode) {
    close();
    _pF = path ? fopen(path, mode) : stderr;
    if (_pF) {
        setvbuf(_pF, nullptr, _IOFBF, 32000);
    }
    return _pF != nullptr;
}


bool File::open(const wchar_t *path, const wchar_t *mode) {
    close();
    _pF = path ? _wfopen(path, mode) : stderr;
    if (_pF) {
        setvbuf(_pF, nullptr, _IOFBF, 32000);
    }
    return _pF != nullptr;
}


bool File::open(StdStream idStream) {
    close();
    switch (idStream) {
    case File::streamIn: _pF = stdin; break;
    case File::streamOut: _pF = stdout; break;
    case File::streamError: _pF = stderr; break;
    }
    return _pF != nullptr;
}


unsigned long File::length()const {
    if (!_pF) {
        return 0;
    }

    unsigned long pos = ftell(_pF);
    fseek(_pF, 0, SEEK_END);
    unsigned long len = ftell(_pF);
    fseek(_pF, pos, SEEK_SET);
    return len;
}


bool File::is_valid() const {
    return _pF != nullptr;
}


unsigned long File::seek(long disp, int seekMethod)const {
    if (seekMethod == end && disp > 0) {
        disp = -disp;
    }
    return fseek(_pF, disp, seekMethod);
}


unsigned long File::print(const char *str, ...) {
    if (!str)return 0;
    va_list args;
    va_start(args, str);
    int len = vfprintf(_pF, str, args);
    va_end(args);
    return len;
}


unsigned long File::print(const wchar_t *str, ...) {
    if (!str)return 0;
    va_list args;
    va_start(args, str);
    int len = vfwprintf(_pF, str, args);
    va_end(args);
    return len;
}


// load whole file as string
std::unique_ptr<char[]> File::load_file(int *len, bool terminate) const {
    if (!_pF) {
        throw std::runtime_error("file doesn't open");
    }

    auto pos = length();
    if (!pos) {
        throw std::logic_error("file length = 0");
    }
    if (len) {
        *len = (int)pos;
    }

    std::unique_ptr<char[]> str(new char[(size_t)pos + (terminate ? 4 : 0)]);
    if (!str) {
        throw std::runtime_error("alloc memory error");
    }
    if (fread(str.get(), (size_t)pos, 1, _pF) < 1) {
        throw std::runtime_error("error file read");
    }
    if (terminate) {
        str[pos] = str[pos + 1] = 0;
    }
    return str;
}


int File::load_file(std::string &s) const {
    if (!_pF) {
        throw std::runtime_error("file doesn't open");
    }

    auto pos = length();
    if (!pos) {
        throw std::logic_error("file length = 0");
    }

    s.resize(pos);
    if (!s.length()) {
        throw std::runtime_error("alloc memory error");
    }
    if (fread((char*)s.c_str(), (size_t)pos, 1, _pF) < 1) {
        throw std::runtime_error("error file read");
    }
    return pos;
}


std::unique_ptr<char[]> File::load_file_nothrow(int *len, bool terminate) const {
    try {
        return load_file(len, terminate);
    } catch (...) {
        if (len)*len = 0;
        return std::unique_ptr<char[]>(nullptr);
    }
}


int File::load_file_nothrow(std::string &s) const {
    try {
        return load_file(s);
    } catch (...) {
        return 0;
    }
}


unsigned long File::writestr(const char *str) {
    unsigned int len = str ? (unsigned int)strlen(str) : 0;
    write(&len, 4);
    if (len) {
        write(str, len);
    }
    return len + 4;
}


unsigned long File::writestr(const wchar_t *str) {
    unsigned int len = str ? (unsigned int)wcslen(str) * 2 : 0;
    write(&len, 4);
    if (len) {
        write(str, len);
    }
    return len + 4;
}


unsigned long File::readstr(char *str) {
    unsigned int len = 0;
    read(&len, 4);
    if (len) {
        read(str, len);
    }
    str[len] = 0;
    return len + 4;
}


unsigned long File::readstr(wchar_t *str) {
    unsigned long len = 0;
    read(&len, 4);
    if (len) {
        read(str, len);
    }
    str[len / 2] = 0;
    return len + 4;
}


unsigned long File::write(const void *data, unsigned long count) const {
    return (unsigned long)fwrite(data, 1, count, _pF);
}


unsigned long File::writeln(const char *str) const {
    return (unsigned long)fwrite(str, 1, strlen(str), _pF);
}


unsigned long File::writeln(const wchar_t *str) const {
    return (unsigned long)fwrite(str, 1, wcslen(str), _pF);
}


unsigned long File::read(void *data, unsigned long count) const {
    return (unsigned long)fread(data, 1, count, _pF);
}


void File::close() {
    if (_pF) {
        fclose(_pF);
    }
    _pF = nullptr;
}


unsigned long File::pos() const {
    return ftell(_pF);
}
