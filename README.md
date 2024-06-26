# matrix-multiplication

## Описание
Небольшое приложение на ```java``` с для умножения матриц на видеокарте с использованием [```aparapi```](https://aparapi.com/)

Представлено три алгоритма:
- наивный алгоритм умножения
- алгоритм и использованием локальной памяти 
- алгоритм с локальной памятью и оптимизацией кол-ва вычислений в потоке

## Docker
Для удобной работы с видеокартой и отсутствия проблем с окружением и драйверами приложен небольшой [```Dockerfile```](https://github.com/VanyaXIII/matrix-multiplication/blob/main/Dockerfile), чтобы правильно им воспользоваться нужно сначала внутри контейнера установить драйвер, так как установщик у него только интерактивный, это делается так
```bash
docker build -t <image_name> . # В папке с Dockerfile, image_name - имя образа, которые вы хотите поставить
docker run -it <image_id> # image_id - id полученного ранее образа
cd /home/opencl_installer && l_opencl_p_18.1.0.015/install.sh # Установка драйвера
```
Следующее удобнее всего сделать в новом терминале, но главное, не выключать контейнер
```bash
docker ps
docker commit <container_id> <new_image_name> #container_id - id нашего контейнера, полученный из предыдущей команды, new_image_name имя для нового изображения
```
Теперь ```new_image_name``` готовый образ, к нему можно лего подключиться из какой-нибудь ide и внутри него запускать код 



