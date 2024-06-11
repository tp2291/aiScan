FROM 244386282524.dkr.ecr.us-west-2.amazonaws.com/ccbu/base/amazon_jre17_base:17.0.10-24.02.16

RUN yum update -y && \
    yum install -y gcc libpq-devel python3-devel

RUN curl -O https://bootstrap.pypa.io/get-pip.py

RUN python3 get-pip.py

WORKDIR /burnout-processor

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD [ "python3", "app.py"]
