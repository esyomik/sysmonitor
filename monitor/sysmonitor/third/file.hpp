#ifndef FILE_06_12_2014_1_35
#define FILE_06_12_2014_1_35


#include <memory>
#include <string>
#include <stdio.h>


/**
 * Wrapper class for the C files.
 */
class
#ifdef SFCDLL_EXPORTS
    __declspec(dllexport)
#endif
    File {
public:
    enum SeekOrigin {
        start,
        current,
        end,
    };
    enum StdStream {
        streamIn,
        streamOut,
        streamError
    };

public:
    File();
    File(const char *path, const char *mode = "rb");
    File(const wchar_t *path, const wchar_t *mode = L"rb");
    File(StdStream idStream);
    ~File();

    void close();
    bool open(const char *path, const char *mode = "rb");
    bool open(const wchar_t *path, const wchar_t *mode = L"rb");
    bool open(StdStream idStream);
    unsigned long seek(long disp, int seekMethod = start)const;

    // basic read, write
    unsigned long write(const void *data, unsigned long count)const;
    unsigned long writeln(const char *str)const;
    unsigned long writeln(const wchar_t *str)const;
    unsigned long writestr(const char *str); // writes: length(int), char[len]
    unsigned long writestr(const wchar_t *str); // writes: length(int), wchar[len]
    unsigned long print(const char *str, ...);
    unsigned long print(const wchar_t *str, ...);
    unsigned long read(void *data, unsigned long count)const;
    unsigned long readstr(char *str); // reads: length(int), char[len]
    unsigned long readstr(wchar_t *str); // reads: length(int), wchar[len]

    // load whole file as string
    std::unique_ptr<char[]> load_file(int *len = 0, bool terminate = true) const;
    int load_file(std::string &s) const;
    std::unique_ptr<char[]> load_file_nothrow(int *len = 0, bool terminate = true) const;
    int load_file_nothrow(std::string &s) const;

    // info
    unsigned long pos()const;
    unsigned long length()const;
    bool is_valid()const;

private:
    FILE * _pF;

};


#endif // FILE_06_12_2014_1_35
