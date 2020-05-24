<h3 align="center">A Language Detection Neural Network using the Encog API and Vector Hashing</h3>
<h4 align="center"><i>Supplementary Assignment in lieu of the written examination</i></h4>

|Details  |    |
| --- | --- |
| **Assignment**  | [Assignment Spec](https://learnonline.gmit.ie/pluginfile.php/206672/mod_resource/content/1/aiAssignment2-2020.pdf) 
| **Course** | BSc (Hons) in Software Development
| **Module** |  Artificial Intelligence |
| **Author** | [Faris Nassif](https://github.com/farisNassif) |
| **Lecturer** | Dr John Healy |

## Overview
The program allows the user to generate a Vector and create a Neural Network by entering input values via a validated console menu. Once a vector is created, the program will hash the vector based on the input values, allowing the Neural Network to process the vector. The Neural Network can then be trained, tested and classifications are possible via file or string input. 

### Requirements for Running Locally
* [Java 8](https://java.com/en/download/faq/java8.xml) (<i>Untested on alternative versions of Java</i>)

### Libraries and Development Tools
* [Encog Core 3.4](https://github.com/jeffheaton/encog-java-core)
  * <i>Included within the `/lib`directory.</i>
  
### How to Run
Run the following in your CLI (<i>Assuming you have Git!</i>)

1. ```git clone https://github.com/farisNassif/FourthYear_Artificial-Intelligence_Supplementary```
2. CD into the project directory
3. ```java -cp .\executables\language-nn.jar;lib\* ie.gmit.sw.Runner```
