struct FileStruct {
	1: required string name;
	2: optional binary content;
	3: optional i64 size;
	4: optional i64 modDate;
	5: optional i64 createdDate;
}
service FilesystemService
{
	list<FileStruct> getDir(1:string path),
	string createDir(1:string path, 2:string name),
	string getFile(1:string path, 2:string filename),
	FileStruct getBinary(1:string path, 2:string filename),
	string putFile(1:string path, 2:string filename, 3:FileStruct file)
}