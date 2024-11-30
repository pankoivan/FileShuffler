#include <iostream>

int main() {
	setlocale(LC_ALL, "Russian");
	system("java -jar file_shuffler.jar");
	std::cout << "\n\nПрограмма завершила свою работу\n\n";
	system("pause");
	return 0;
}
