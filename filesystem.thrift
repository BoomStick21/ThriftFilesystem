namespace if4031 filesystem
typedef i32 int
service FilesystemService
{
	list<string> getDir(1:string path),
	string createDir(1:string path, 2:string name),
	string getFile(1:string path, 2:string filename),
	binary getBinary(1:string path, 2:string filename),
	string putFile(1:string path, 2:string filename, 3:binary file)
}