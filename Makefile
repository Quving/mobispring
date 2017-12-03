build_mobispring:
	docker build -t pingu/mobispring:latest .

run_mobispring:
	docker run --name mobispring -p 8080:8080 -v $(shell pwd):/workdir pingu/mobispring


