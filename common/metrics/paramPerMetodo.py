import glob
import sys
import lxml.etree as ET

percorso = sys.argv[1]

def fixXML():
  reload(sys)
  sys.setdefaultencoding("utf-8")
  for file in glob.glob(percorso+"/*.xml"):
    getParamsNumber(file)

def getParamsNumber(path):
  xmlDoc = ET.parse(path)
  mass = 0.0
  med = 0.0
  num = 0

  root = xmlDoc.getroot()

  constructors = root.xpath("//constructor")
  methods = root.xpath("//method")

  for ctor in constructors:
    med = medParam(ctor,med)
    mass = maxParam(ctor,mass)
    num = num + 1

  for met in methods:
    med = medParam(met,med)
    mass = maxParam(met,mass)
    num = num + 1

    med = med / num

  if str(percorso) == "sections/android-packages/":
    out_file = open("metricsAndroid.txt","w")
  if str(percorso) == "sections/cloud-packages/":
    out_file = open("metricsCloud.txt","w")

  out_file.write(str(mass) + " " + str(med) )

def medParam(node,med):
  parameters = node.xpath(".//parameter")
  number = len(parameters)
  return med + number

def maxParam(node,mass):
  parameters = node.xpath(".//parameter")
  number = len(parameters)
  if mass < number:
    mass = number
  return mass

fixXML()
